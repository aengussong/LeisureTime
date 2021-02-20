package com.aengussong.prioritytime

import androidx.lifecycle.*
import com.aengussong.prioritytime.data.local.entity.TaskEntity
import com.aengussong.prioritytime.model.Task
import com.aengussong.prioritytime.model.SortOrder
import com.aengussong.prioritytime.usecase.*
import com.aengussong.prioritytime.util.Tree
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TaskDataViewModel(
    private val addTaskUseCase: AddTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val incrementTaskUseCase: IncrementTaskUseCase,
    private val renameTaskUseCase: RenameTaskUseCase,
    private val removeTaskUseCase: RemoveTaskUseCase,
    private val dropCountersUseCase: DropCountersUseCase,
    private val decrementUseCase: DecrementUseCase,
    private val sortOrderUseCase: SortOrderUseCase
) : ViewModel() {

    val taskLiveData: LiveData<List<Tree<Task>>>
        get() = _taskLiveData
    val errorLiveData: LiveData<Throwable>
        get() = _errorLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    private val _taskLiveData = sortOrderUseCase.getSortOrder().flatMapLatest { order ->
        when (order) {
            SortOrder.HIERARCHY -> getTaskUseCase.getHierarchialTasks()
            SortOrder.LINEAR -> getTaskUseCase.getLinearTasks()
        }.distinctUntilChanged()
    }.asLiveData()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        _errorLiveData.value = throwable
    }

    fun addTask(name: String, parentId: Long? = null): Job {
        return launchWithHandler {
            addTaskUseCase.execute(name, parentId)
        }
    }

    fun incrementCounter(taskId: Long): Job {
        return launchWithHandler {
            incrementTaskUseCase.execute(taskId)
        }
    }

    fun renameTask(taskId: Long, newName: String): Job {
        return launchWithHandler {
            renameTaskUseCase.execute(taskId, newName)
        }
    }

    fun removeEntity(taskId: Long): Job {
        return launchWithHandler {
            removeTaskUseCase.execute(taskId)
        }
    }

    fun dropCounters() = launchWithHandler {
        dropCountersUseCase.execute()
    }

    fun observeTask(id: Long): Flow<TaskEntity?> = getTaskUseCase.getTask(id)

    fun toggleSort() {
        sortOrderUseCase.toggleSortOrder()
    }

    private fun launchWithHandler(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(errorHandler, block = block)

    fun decrementTask(taskId: Long) = launchWithHandler {
        decrementUseCase.execute(taskId)
    }
}