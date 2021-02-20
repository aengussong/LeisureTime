package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.TaskRepository
import com.aengussong.prioritytime.testUtils.TaskProvider
import com.aengussong.prioritytime.util.AncestryBuilder
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RemoveTaskUseCaseTest {

    private val repo = mockk<TaskRepository>()
    private val useCase = RemoveTaskUseCase(repo)

    @Test
    fun `remove task - should remove entity`() = runBlocking {
        val task = TaskProvider.getGenericEntity()
            .copy(ancestry = AncestryBuilder().withChild(4).toString())
        coEvery { repo.removeTask(task.id) } just Runs
        coEvery { repo.removeTasks(any()) } just Runs
        coEvery { repo.getAncestry(task.id) } returns task.ancestry

        useCase.execute(task.id)

        coVerify { repo.removeTask(task.id) }
        coVerify { repo.removeTasks(any()) }
    }

    @Test
    fun `remove root task - only this root task and it's children should be deleted`() = runBlocking {
        val task =
            TaskProvider.getGenericEntity().copy(ancestry = AncestryBuilder().toString())
        val taskAncestryForChildren = AncestryBuilder(task.ancestry).withChild(task.id).toString()
        coEvery { repo.removeTask(task.id) } just Runs
        coEvery { repo.removeTasks(any()) } just Runs
        coEvery { repo.getAncestry(task.id) } returns task.ancestry

        useCase.execute(task.id)

        coVerify(exactly = 1) { repo.removeTask(task.id) }
        coVerify(exactly = 0) { repo.removeTasks(neq(taskAncestryForChildren)) }
    }

    @Test
    fun `remove root task - root's children also should be removed`() = runBlocking {
        val parentId = 0L
        val parentAncestry = AncestryBuilder().withChild(parentId).toString()
        coEvery { repo.getAncestry(parentId) } returns AncestryBuilder().toString()
        coEvery { repo.removeTask(parentId) } just Runs
        coEvery { repo.removeTasks(parentAncestry) } just Runs

        useCase.execute(parentId)

        coVerify { repo.removeTask(parentId) }
        coVerify { repo.removeTasks(parentAncestry) }
    }

}