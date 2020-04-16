package com.aengussong.leisuretime.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aengussong.leisuretime.data.local.DateConverter
import com.aengussong.leisuretime.data.local.dao.LeisureDao
import com.aengussong.leisuretime.data.local.entity.LeisureEntity


@Database(entities = [LeisureEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class LeisureDb : RoomDatabase() {
    abstract fun leisureDao(): LeisureDao
}