package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.TaskRepository
import com.aengussong.prioritytime.data.local.entity.TaskEntity
import com.aengussong.prioritytime.util.AncestryBuilder

class AddTaskUseCase(private val repo: TaskRepository) {

    suspend fun execute(name: String, parentId: Long? = null) {
        val task = parentId?.let {
            createSubTask(name, it)
        } ?: createRootTask(name)
        repo.addTask(task)
    }

    private suspend fun createSubTask(name: String, parentId: Long): TaskEntity {
        val parentAncestry = repo.getAncestry(parentId)
        val ancestry = AncestryBuilder(parentAncestry).withChild(parentId).toString()
        val counter = repo.getLowestCounter(ancestry).let {
            if (it == 0L) {
                return@let repo.getTaskCounter(parentId)
            }
            it
        }
        return TaskEntity(name, counter, ancestry)
    }

    private suspend fun createRootTask(name: String): TaskEntity {
        //root ancestry
        val ancestry = AncestryBuilder().toString()
        val counter = repo.getLowestCounter(ancestry)
        return TaskEntity(name, counter, ancestry)
    }

}