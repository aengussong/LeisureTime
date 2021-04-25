package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.TaskRepository

class DecrementUseCase(private val repo: TaskRepository) {

    suspend fun execute(taskId: Long) {
        repo.getTaskCounter(taskId).also {
            if (it > 0L) {
                repo.setCounter(taskId, it - 1)
            }
        }
    }
}
