package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.TaskRepository
import com.aengussong.prioritytime.util.AncestryBuilder

class IncrementTaskUseCase(private val repo: TaskRepository) {

    suspend fun execute(id: Long) {
        val entity = repo.getTask(id)
        val idsToIncrement =
            AncestryBuilder(entity.ancestry).withChild(entity.id).getAncestryIds()

        repo.incrementTasks(idsToIncrement)
    }

    suspend fun incrementMinTask() {
        repo.getMinTask()?.id?.let {
            execute(it)
        }
    }
}
