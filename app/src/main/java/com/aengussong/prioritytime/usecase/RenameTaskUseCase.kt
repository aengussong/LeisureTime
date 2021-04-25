package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.TaskRepository

class RenameTaskUseCase(private val repo: TaskRepository) {

    suspend fun execute(taskId: Long, newName: String) =
        repo.renameTask(taskId, newName)
}
