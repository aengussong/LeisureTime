package com.aengussong.prioritytime

import androidx.lifecycle.*
import com.aengussong.prioritytime.model.Leisure
import com.aengussong.prioritytime.usecase.*
import com.aengussong.prioritytime.model.SortOrder
import com.aengussong.prioritytime.util.Tree
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LeisureDataViewModel(
    private val addLeisureUseCase: AddLeisureUseCase,
    private val getLeisureUseCase: GetLeisureUseCase,
    private val incrementLeisureUseCase: IncrementLeisureUseCase,
    private val renameLeisureUseCase: RenameLeisureUseCase,
    private val removeLeisureUseCase: RemoveLeisureUseCase,
    private val dropCountersUseCase: DropCountersUseCase,
    private val decrementUseCase: DecrementUseCase,
    private val sortOrderUseCase: SortOrderUseCase
) : ViewModel() {

    val leisureLiveData: LiveData<List<Tree<Leisure>>>
        get() = _leisureLiveData
    val errorLiveData: LiveData<Throwable>
        get() = _errorLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    private val _leisureLiveData = MediatorLiveData<List<Tree<Leisure>>>()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        _errorLiveData.value = throwable
    }

    init {
        val hierarchy = getLeisureUseCase.getHierarchialLeisures()
        val linear = getLeisureUseCase.getLinearLeisures().asLiveData()

        viewModelScope.launch {
            sortOrderUseCase.getSortOrder().collect { sortOrder ->
                when (sortOrder) {
                    SortOrder.HIERARCHY -> {
                        _leisureLiveData.removeSource(linear)
                        _leisureLiveData.addSource(hierarchy) { _leisureLiveData.value = it }
                    }
                    SortOrder.LINEAR -> {
                        _leisureLiveData.removeSource(hierarchy)
                        _leisureLiveData.addSource(linear) { _leisureLiveData.value = it }
                    }
                }
            }
        }
    }

    fun addLeisure(name: String, parentId: Long? = null): Job {
        return launchWithHandler {
            addLeisureUseCase.execute(name, parentId)
        }
    }

    fun incrementCounter(leisureId: Long): Job {
        return launchWithHandler {
            incrementLeisureUseCase.execute(leisureId)
        }
    }

    fun renameLeisure(leisureId: Long, newName: String): Job {
        return launchWithHandler {
            renameLeisureUseCase.execute(leisureId, newName)
        }
    }

    fun removeEntity(leisureId: Long): Job {
        return launchWithHandler {
            removeLeisureUseCase.execute(leisureId)
        }
    }

    fun dropCounters() = launchWithHandler {
        dropCountersUseCase.execute()
    }

    fun observeLeisure(id: Long) = getLeisureUseCase.getLeisure(id)

    fun toggleSort() {
        sortOrderUseCase.toggleSortOrder()
    }

    private fun launchWithHandler(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(errorHandler, block = block)

    fun decrementLeisure(leisureId: Long) = launchWithHandler {
        decrementUseCase.execute(leisureId)
    }
}