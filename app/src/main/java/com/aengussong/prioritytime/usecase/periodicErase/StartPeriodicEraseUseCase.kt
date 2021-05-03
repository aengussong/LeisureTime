package com.aengussong.prioritytime.usecase.periodicErase

import com.aengussong.prioritytime.data.TaskRepository
import com.aengussong.prioritytime.worker.Work
import com.aengussong.prioritytime.worker.WorkerHelper

class StartPeriodicEraseUseCase(
    private val repo: TaskRepository,
    private val workerHelper: WorkerHelper
) {

    suspend fun execute(work: Work) {
        repo.saveEraseOption(work)
        workerHelper.startWork(work)
    }
}