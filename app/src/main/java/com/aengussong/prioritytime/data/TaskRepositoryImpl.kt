package com.aengussong.prioritytime.data

import com.aengussong.prioritytime.data.local.SharedPrefs
import com.aengussong.prioritytime.data.local.dao.TasksDao
import com.aengussong.prioritytime.data.local.entity.TaskEntity
import com.aengussong.prioritytime.model.SortOrder
import com.aengussong.prioritytime.util.ROOT_ANCESTRY
import com.aengussong.prioritytime.worker.Work
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext

class TaskRepositoryImpl(
    private val localProvider: TasksDao,
    private val prefs: SharedPrefs
) : TaskRepository {

    override suspend fun addTask(task: TaskEntity) = onIO {
        localProvider.addTask(task)
    }

    /**
     * @return lowest counter for the current ancestry or 0 if there are no items with such ancestry.
     * */
    override suspend fun getLowestCounter(ancestry: String): Long = onIO {
        localProvider.getLowestCounter(ancestry)
    }

    override suspend fun getAncestry(id: Long): String = onIO {
        localProvider.getAncestry(id)
    }

    override fun getHierarchialTasks(): Flow<List<TaskEntity>> {
        return localProvider.getHierarchialTasks()
    }

    override fun getLinearTasks(): Flow<List<TaskEntity>> {
        return localProvider.getLinearTasks()
    }

    override suspend fun getTask(id: Long): TaskEntity = onIO {
        localProvider.getTask(id)
    }

    override suspend fun incrementTasks(ids: List<Long>) = onIO {
        localProvider.incrementTasks(ids)
    }

    override suspend fun renameTask(id: Long, newName: String) = onIO {
        localProvider.renameTask(id, newName)
    }

    override suspend fun removeTask(id: Long) = onIO {
        localProvider.removeTask(id)
    }

    override suspend fun removeTasks(ancestry: String) = onIO {
        localProvider.removeTasks(ancestry)
    }

    override suspend fun dropCounters() = onIO {
        localProvider.dropCounters()
    }

    override suspend fun getTaskCounter(id: Long) = onIO {
        localProvider.getCounter(id)
    }

    override suspend fun setCounter(id: Long, counter: Long) = onIO {
        localProvider.updateCounter(id, counter)
    }

    override fun observeTask(id: Long) = localProvider.observeTaskDistinct(id)

    override fun observeMinTask(): Flow<TaskEntity?> {
        return getSortOrder().flatMapLatest { order ->
            when (order) {
                SortOrder.HIERARCHY -> localProvider.observeMinHierarchial(ROOT_ANCESTRY)
                SortOrder.LINEAR -> localProvider.observeMinLinear()
            }.distinctUntilChanged()
        }
    }

    override suspend fun getMinTask(): TaskEntity? {
        return when (prefs.getSortOrder()) {
            SortOrder.HIERARCHY -> localProvider.getMinHierarchial(ROOT_ANCESTRY)
            SortOrder.LINEAR -> localProvider.getMinLinear()
        }
    }

    override fun toggleSort() {
        val orderToSave = if (prefs.getSortOrder() == SortOrder.LINEAR) {
            SortOrder.HIERARCHY
        } else {
            SortOrder.LINEAR
        }
        prefs.saveSortOrder(orderToSave)
    }

    override fun getSortOrder(): Flow<SortOrder> {
        return prefs.observeSortOrder()
    }

    override suspend fun saveEraseOption(work: Work): Unit = onIO {
        prefs.saveEraseOption(work)
    }

    @ExperimentalCoroutinesApi
    override fun getEraseOption(): Flow<Work> {
        return prefs.getEraseOptionFlow()
    }

    private suspend fun <T> onIO(block: suspend CoroutineScope.() -> T) =
        withContext(Dispatchers.IO, block = block)
}