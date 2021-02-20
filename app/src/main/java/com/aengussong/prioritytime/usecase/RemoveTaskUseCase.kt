package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.TaskRepository
import com.aengussong.prioritytime.util.AncestryBuilder

class RemoveTaskUseCase(private val repo: TaskRepository) {

    suspend fun execute(id: Long) {
        val ancestry = repo.getAncestry(id)

        //remove children of task
        val childrenAncestry = AncestryBuilder(ancestry).withChild(id).toString()
        repo.removeTasks(childrenAncestry)

        //remove task
        repo.removeTask(id)
    }
}