package com.aengussong.prioritytime.util.module

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aengussong.prioritytime.data.local.TasksDb
import org.koin.dsl.module

val inMemoryDb = module {
    single(override = true) {
        Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TasksDb::class.java
        ).build()
    }
    single(override = true) {
        get<TasksDb>().taskDao()
    }
}