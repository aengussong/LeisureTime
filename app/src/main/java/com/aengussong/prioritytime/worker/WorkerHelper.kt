package com.aengussong.prioritytime.worker

import android.content.Context
import androidx.annotation.StringRes
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.aengussong.prioritytime.R
import java.util.concurrent.TimeUnit

const val ERASE_DATA_WORKER_NAME = "erase_data_worker"

class WorkerHelper constructor(context: Context) {
    private val workManager = WorkManager.getInstance(context)

    fun startWork(work: Work) {
        when (work) {
            Work.ERASE_MONTHLY -> {
                val request = PeriodicWorkRequestBuilder<EraseDataWorker>(30, TimeUnit.DAYS)
                    .setInitialDelay(30, TimeUnit.DAYS)
                    .build()
                workManager.enqueueUniquePeriodicWork(
                    ERASE_DATA_WORKER_NAME,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    request
                )
            }
            Work.ERASE_NEVER -> {
                workManager.cancelUniqueWork(ERASE_DATA_WORKER_NAME)
            }
        }
    }
}

enum class Work(@StringRes val titleRes: Int) {
    ERASE_MONTHLY(R.string.monthly_option),
    ERASE_NEVER(R.string.never_option)
}