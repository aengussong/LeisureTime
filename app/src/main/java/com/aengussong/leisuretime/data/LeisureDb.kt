package com.aengussong.leisuretime.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aengussong.leisuretime.data.dao.LeisureDao
import com.aengussong.leisuretime.data.entity.LeisureEntity


@Database(entities = [LeisureEntity::class], version = 1)
abstract class LeisureDb : RoomDatabase() {
    abstract fun leisureDao(): LeisureDao
}