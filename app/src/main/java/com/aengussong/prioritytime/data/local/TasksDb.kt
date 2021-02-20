package com.aengussong.prioritytime.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aengussong.prioritytime.data.local.dao.TasksDao
import com.aengussong.prioritytime.data.local.entity.TaskEntity


@Database(entities = [TaskEntity::class], version = 2, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class TasksDb : RoomDatabase() {
    abstract fun taskDao(): TasksDao
}