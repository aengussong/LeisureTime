package com.aengussong.prioritytime.app

import android.app.Application
import androidx.lifecycle.*
import com.aengussong.prioritytime.di.*
import com.aengussong.prioritytime.usecase.GetTaskUseCase
import com.aengussong.prioritytime.widget.PriorityWidgetProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.KoinExperimentalAPI
import org.koin.core.context.startKoin


class TaskApp : Application() {

    private val getTaskUseCase: GetTaskUseCase by inject()

    private val coroutineExceptionHelper = CoroutineExceptionHandler { _, _ ->
        //swallow the exception, can't handle it here, or do anything useful
    }
    private val appScope =
        CoroutineScope(Dispatchers.Main + SupervisorJob() + coroutineExceptionHelper)

    @KoinExperimentalAPI
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TaskApp)
            workManagerFactory()
            modules(
                dbModule,
                dataModule,
                utilsModule,
                workerModule,
                viewModelModule
            )
        }

        getTaskUseCase.observeMinTask().onEach {
            broadcastNewInfoToWidgets()
        }.launchIn(appScope)
    }

    private fun broadcastNewInfoToWidgets() {
        sendBroadcast(PriorityWidgetProvider.getUpdateWidgetIntent(this))
    }

}