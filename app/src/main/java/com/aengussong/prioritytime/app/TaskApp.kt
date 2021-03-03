package com.aengussong.prioritytime.app

import android.app.Application
import androidx.lifecycle.*
import com.aengussong.prioritytime.di.dataModule
import com.aengussong.prioritytime.di.dbModule
import com.aengussong.prioritytime.di.viewModelModule
import com.aengussong.prioritytime.usecase.GetTaskUseCase
import com.aengussong.prioritytime.widget.PriorityWidgetProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class TaskApp : Application() {

    private val getTaskUseCase: GetTaskUseCase by inject()

    private val coroutineExceptionHelper = CoroutineExceptionHandler { _, _ ->
        //swallow the exception, can't handle it here, or do anything useful
    }
    private val appScope =
        CoroutineScope(Dispatchers.Main + SupervisorJob() + coroutineExceptionHelper)

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TaskApp)
            modules(
                dbModule,
                dataModule,
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