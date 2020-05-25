package com.aengussong.leisuretime.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface LeisureDao {

    @Insert
    suspend fun addLeisure(leisure: LeisureEntity)

    @Query("SELECT * FROM leisureentity ORDER BY counter, updated ASC")
    fun getLeisures(): LiveData<List<LeisureEntity>>

    /**
     * Select lowest counter for level, e.g. if item added as child for second level element, we
     * should find lowest counter in third level for this parent element, without elements
     * examination on other levels
     * */
    @Query("SELECT MIN(counter) FROM leisureentity WHERE ancestry = :ancestry")
    suspend fun getLowestCounter(ancestry:String): Long

    @Query("SELECT ancestry FROM leisureentity WHERE id = :id")
    suspend fun getAncestry(id:Long):String

}