package com.aengussong.leisuretime.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aengussong.leisuretime.data.entity.LeisureEntity
import java.util.*

@Dao
interface LeisureDao {

    @Insert
    fun insert(leisureEntity: LeisureEntity)

    @Query("SELECT * FROM leisureentity ORDER BY counter, updated ASC")
    fun getLeisures(): LiveData<List<LeisureEntity>>

    @Query("UPDATE leisureentity SET counter = :counter, updated=:date WHERE name = :name")
    fun updateLeisure(name:String, counter: Int, date: Date = Date())
}