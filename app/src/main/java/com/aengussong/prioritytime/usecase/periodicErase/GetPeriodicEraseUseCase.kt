package com.aengussong.prioritytime.usecase.periodicErase

import com.aengussong.prioritytime.data.TaskRepository
import com.aengussong.prioritytime.worker.Work
import kotlinx.coroutines.flow.Flow

class GetPeriodicEraseUseCase(private val repo: TaskRepository) {

    fun execute(): Flow<Work> {
        return repo.getEraseOption()
    }
}