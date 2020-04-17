package com.aengussong.leisuretime.app

import android.app.Application
import com.aengussong.leisuretime.di.dataModule
import com.aengussong.leisuretime.di.dbModule
import com.aengussong.leisuretime.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class LeisureApp : Application() {

    override fun onCreate() {
        super.onCreate()

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