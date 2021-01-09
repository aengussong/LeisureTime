package com.aengussong.prioritytime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aengussong.prioritytime.data.local.entity.LeisureEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface LeisureDao {

    @Insert
    suspend fun addLeisure(leisure: LeisureEntity): Long

    @Query("SELECT * FROM leisureentity ORDER BY ancestry")
    fun getHierarchialLeisures(): Flow<List<LeisureEntity>>

    @Query("SELECT * FROM leisureentity ORDER BY counter")
    fun getLinearLeisures(): Flow<List<LeisureEntity>>

    /**
     * Select lowest counter for level, e.g. if item added as child for second level element, we
     * should find lowest counter in third level for this parent element, without elements
     * examination on other levels. If there is no items, returns 0.
     * */
    @Query("SELECT COALESCE(MIN(counter),0) FROM leisureentity WHERE ancestry = :ancestry")
    suspend fun getLowestCounter(ancestry: String): Long

    @Query("SELECT ancestry FROM leisureentity WHERE id = :id")
    suspend fun getAncestry(id: Long): String

    @Query("UPDATE leisureentity SET counter = counter+1,  updated = :date WHERE id IN (:ids)")
    suspend fun incrementLeisures(ids: List<Long>, date: Date = Date())

    @Query("SELECT * FROM leisureentity WHERE id = :id")
    suspend fun getLeisure(id: Long): LeisureEntity

    @Query("UPDATE leisureentity SET name = :newName WHERE id = :id")
    suspend fun renameLeisure(id: Long, newName: String)

    @Query("DELETE FROM leisureentity WHERE id = :id")
    suspend fun removeLeisure(id: Long)

    @Query("DELETE FROM LeisureEntity WHERE ancestry LIKE :ancestry||'%'")
    suspend fun removeLeisures(ancestry: String)

    @Query("UPDATE leisureentity SET counter = 0")
    suspend fun dropCounters()

    @Query("SELECT counter FROM leisureentity WHERE id = :id")
    suspend fun getCounter(id: Long): Long

    @Query("UPDATE leisureentity SET counter = :counter WHERE id = :id")
    suspend fun updateCounter(id: Long, counter: Long)

    @Query("SELECT * FROM leisureentity WHERE id = :id")
    fun observeLeisure(id: Long): Flow<LeisureEntity?>

    @Query("SELECT * FROM leisureentity WHERE ancestry = :ancestry ORDER BY counter LIMIT 1")
    suspend fun getMinHierarchial(ancestry: String): LeisureEntity?

    @Query("SELECT * FROM leisureentity WHERE ancestry = :ancestry ORDER BY counter LIMIT 1")
    fun observeMinHierarchial(ancestry: String): Flow<LeisureEntity?>

    @Query("SELECT * FROM leisureentity ORDER BY counter LIMIT 1")
    suspend fun getMinLinear(): LeisureEntity?

    @Query("SELECT * FROM leisureentity ORDER BY counter LIMIT 1")
    fun observeMinLinear(): Flow<LeisureEntity?>

    fun observeLeisureDistinct(id: Long) = observeLeisure(id).distinctUntilChanged()
}