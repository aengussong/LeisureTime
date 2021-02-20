package com.aengussong.prioritytime.widget

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.aengussong.prioritytime.usecase.IncrementTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

private const val JOB_ID = 1045

class PriorityWidgetService : JobIntentService() {

    companion object {
        fun enqueueWork(context: Context) {
            enqueueWork(context, PriorityWidgetService::class.java, JOB_ID, Intent())
        }
    }

    private val incrementTaskEntity: IncrementTaskUseCase by inject()

    override fun onHandleWork(intent: Intent) {
        //Job intent service is already on the background service, so should use run blocking
        runBlocking {
            incrementTaskEntity.incrementMinTask()
            withContext(Dispatchers.Main) {
                sendBroadcast(PriorityWidgetProvider.getUpdateWidgetIntent(this@PriorityWidgetService))
            }
        }
    }

}