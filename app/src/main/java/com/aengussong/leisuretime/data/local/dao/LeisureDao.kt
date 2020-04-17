package com.aengussong.leisuretime.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import io.reactivex.Single
import java.util.*

@Dao
interface LeisureDao {

    @Insert
    fun addLeisure(leisure: LeisureEntity)

    @Query("SELECT * FROM leisureentity WHERE name=:name")
    fun getLeisure(name: String): Single<LeisureEntity>

    @Query("SELECT * FROM leisureentity ORDER BY counter, updated ASC")
    fun getLeisures(): LiveData<List<LeisureEntity>>

    @Query("UPDATE leisureentity SET counter = :counter, updated=:date WHERE name = :name")
    fun updateLeisure(name: String, counter: Int, date: Date = Date())
}