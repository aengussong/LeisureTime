package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.TaskRepository

class DropCountersUseCase(private val repo: TaskRepository) {

    suspend fun execute() = repo.dropCounters()
}
