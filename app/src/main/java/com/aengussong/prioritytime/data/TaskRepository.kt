package com.aengussong.prioritytime.data

import com.aengussong.prioritytime.data.local.entity.TaskEntity
import com.aengussong.prioritytime.model.SortOrder
import com.aengussong.prioritytime.worker.Work
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun addTask(task: TaskEntity): Long
    suspend fun getLowestCounter(ancestry: String): Long
    suspend fun getAncestry(id: Long): String
    fun getHierarchialTasks(): Flow<List<TaskEntity>>
    fun getLinearTasks(): Flow<List<TaskEntity>>
    suspend fun getTask(id: Long): TaskEntity
    suspend fun incrementTasks(ids: List<Long>)
    suspend fun renameTask(id: Long, newName: String)
    suspend fun removeTask(id: Long)
    suspend fun removeTasks(ancestry: String)
    suspend fun dropCounters()
    suspend fun getTaskCounter(id: Long): Long
    suspend fun setCounter(id: Long, counter: Long)
    suspend fun getMinTask(): TaskEntity?
    fun observeTask(id: Long): Flow<TaskEntity?>
    fun observeMinTask(): Flow<TaskEntity?>
    fun toggleSort()
    fun getSortOrder(): Flow<SortOrder>

    suspend fun saveEraseOption(work: Work)
    fun getEraseOption(): Flow<Work>
}