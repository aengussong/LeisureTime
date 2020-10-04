package com.aengussong.prioritytime.app

import android.app.Application
import com.aengussong.prioritytime.di.dataModule
import com.aengussong.prioritytime.di.dbModule
import com.aengussong.prioritytime.di.viewModelModule
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class LeisureApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        startKoin {
            androidContext(this@LeisureApp)
            modules(
                dbModule,
                dataModule,
                viewModelModule
            )
        }
    }
}