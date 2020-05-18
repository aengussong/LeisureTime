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
    fun addLeisure(leisure: LeisureEntity): Completable

    @Query("SELECT * FROM leisureentity WHERE id=:id")
    fun getLeisure(id: Long): Single<LeisureEntity>

    @Query("SELECT * FROM leisureentity ORDER BY counter, updated ASC")
    fun getLeisures(): LiveData<List<LeisureEntity>>

    @Query("UPDATE leisureentity SET name = :newName WHERE id = :id")
    fun updateLeisure(id: Long, newName: String): Completable

    @Query("DELETE FROM leisureentity WHERE id = :id")
    fun deleteLeisure(id: Long): Completable

}