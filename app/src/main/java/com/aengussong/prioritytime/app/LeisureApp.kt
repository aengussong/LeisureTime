package com.aengussong.prioritytime.app

import android.app.Application
import androidx.lifecycle.*
import com.aengussong.prioritytime.di.dataModule
import com.aengussong.prioritytime.di.dbModule
import com.aengussong.prioritytime.di.viewModelModule
import com.aengussong.prioritytime.usecase.GetLeisureUseCase
import com.aengussong.prioritytime.widget.PriorityWidgetProvider
import com.facebook.stetho.Stetho
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class LeisureApp : Application() {

    private val getLeisureUseCase: GetLeisureUseCase by inject()

    private val coroutineExceptionHelper = CoroutineExceptionHandler { _, _ ->
        //swallow the exception, can't handle it here, or do anything useful
    }
    private val appScope =
        CoroutineScope(Dispatchers.Main + SupervisorJob() + coroutineExceptionHelper)

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

        getLeisureUseCase.observeMinLeisure().onEach {
            broadcastNewInfoToWidgets()
        }.launchIn(appScope)
    }

    private fun broadcastNewInfoToWidgets() {
        sendBroadcast(PriorityWidgetProvider.getUpdateWidgetIntent(this))
    }

}