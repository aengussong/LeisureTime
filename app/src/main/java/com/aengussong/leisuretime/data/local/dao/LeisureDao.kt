package com.aengussong.leisuretime.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import java.util.*

@Dao
interface LeisureDao {

    @Insert
    suspend fun addLeisure(leisure: LeisureEntity)

    @Query("SELECT * FROM leisureentity ORDER BY ancestry")
    fun getLeisures(): LiveData<List<LeisureEntity>>

    /**
     * Select lowest counter for level, e.g. if item added as child for second level element, we
     * should find lowest counter in third level for this parent element, without elements
     * examination on other levels
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

    @Query("DELETE FROM leisureentity WHERE ancestry LIKE (SELECT ancestry FROM leisureentity WHERE id = :id)||'%'")
    suspend fun removeLeisure(id: Long)

    @Query("DELETE FROM leisureentity WHERE id = :id")
    suspend fun removeRootLeisure(id: Long)

    @Query("UPDATE leisureentity SET counter = 0")
    suspend fun dropCounters()

    @Query("SELECT counter FROM leisureentity WHERE id = :id")
    suspend fun getCounter(id: Long): Long

    @Query("UPDATE leisureentity SET counter = :counter WHERE id = :id")
    suspend fun updateCounter(id: Long, counter: Long)
}