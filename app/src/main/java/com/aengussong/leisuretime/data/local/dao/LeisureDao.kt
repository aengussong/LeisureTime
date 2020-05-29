package com.aengussong.leisuretime.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aengussong.leisuretime.data.local.entity.LeisureEntity

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

    @Query("UPDATE leisureentity SET counter = counter+1 WHERE id IN (:ids)")
    suspend fun incrementLeisures(ids: List<Long>)

    @Query("SELECT * FROM leisureentity WHERE id = :id")
    suspend fun getLeisure(id: Long): LeisureEntity

}