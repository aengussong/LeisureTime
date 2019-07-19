package com.aengussong.leisuretime.app

import android.app.Application
import androidx.room.RoomDatabase
import androidx.room.Room
import com.aengussong.leisuretime.data.LeisureDb


class LeisureApp: Application() {
    companion object{
        lateinit var db:RoomDatabase

        fun getDatabase():RoomDatabase = db
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            this,
            LeisureDb::class.java, "leisure_db"
        ).build()
    }
}