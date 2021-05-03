package com.aengussong.prioritytime.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.aengussong.prioritytime.data.TaskRepository
import kotlinx.coroutines.runBlocking

class EraseDataWorker(
    private val repo: TaskRepository,
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    override fun doWork(): Result = runBlocking {
        repo.dropCounters()
        Result.success()
    }
}