package com.aengussong.leisuretime.util.module

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aengussong.leisuretime.data.local.LeisureDb
import org.koin.dsl.module

val mockDbModule = module {
    single(override = true) {
        Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LeisureDb::class.java
        ).build()
    }
}