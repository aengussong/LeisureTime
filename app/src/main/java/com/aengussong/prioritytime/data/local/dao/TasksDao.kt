package com.aengussong.prioritytime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aengussong.prioritytime.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface TasksDao {

    @Insert
    suspend fun addTask(task: TaskEntity): Long

    @Query("SELECT * FROM tasks ORDER BY ancestry")
    fun getHierarchialTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY counter")
    fun getLinearTasks  (): Flow<List<TaskEntity>>

    /**
     * Select lowest counter for level, e.g. if item added as child for second level element, we
     * should find lowest counter in third level for this parent element, without elements
     * examination on other levels. If there is no items, returns 0.
     * */
    @Query("SELECT COALESCE(MIN(counter),0) FROM tasks WHERE ancestry = :ancestry")
    suspend fun getLowestCounter(ancestry: String): Long

    @Query("SELECT ancestry FROM tasks WHERE id = :id")
    suspend fun getAncestry(id: Long): String

    @Query("UPDATE tasks SET counter = counter+1,  updated = :date WHERE id IN (:ids)")
    suspend fun incrementTasks(ids: List<Long>, date: Date = Date())

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTask(id: Long): TaskEntity

    @Query("UPDATE tasks SET name = :newName WHERE id = :id")
    suspend fun renameTask(id: Long, newName: String)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun removeTask(id: Long)

    @Query("DELETE FROM tasks WHERE ancestry LIKE :ancestry||'%'")
    suspend fun removeTasks(ancestry: String)

    @Query("UPDATE tasks SET counter = 0")
    suspend fun dropCounters()

    @Query("SELECT counter FROM tasks WHERE id = :id")
    suspend fun getCounter(id: Long): Long

    @Query("UPDATE tasks SET counter = :counter WHERE id = :id")
    suspend fun updateCounter(id: Long, counter: Long)

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun observeTask(id: Long): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE ancestry = :ancestry ORDER BY counter, updated LIMIT 1")
    suspend fun getMinHierarchial(ancestry: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE ancestry = :ancestry ORDER BY counter, updated LIMIT 1")
    fun observeMinHierarchial(ancestry: String): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks ORDER BY counter, updated LIMIT 1")
    suspend fun getMinLinear(): TaskEntity?

    @Query("SELECT * FROM tasks ORDER BY counter, updated LIMIT 1")
    fun observeMinLinear(): Flow<TaskEntity?>

    fun observeTaskDistinct(id: Long) = observeTask(id).distinctUntilChanged()
}