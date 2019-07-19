package com.aengussong.leisuretime.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.aengussong.leisuretime.data.dao.LeisureDao
import com.aengussong.leisuretime.data.entity.LeisureEntity


@Database(entities = [LeisureEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class LeisureDb : RoomDatabase() {
    abstract fun leisureDao(): LeisureDao
}