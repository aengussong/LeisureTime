package com.aengussong.leisuretime.app

import android.app.Application
import androidx.room.Room
import com.aengussong.leisuretime.data.LeisureDb
import com.aengussong.leisuretime.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class LeisureApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@LeisureApp)
            modules(dataModule)
        }

        val db = Room.databaseBuilder(
            this,
            LeisureDb::class.java, "leisure_db"
        ).build()
    }
}