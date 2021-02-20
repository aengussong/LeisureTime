package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.TaskRepository
import com.aengussong.prioritytime.testUtils.TaskProvider
import com.aengussong.prioritytime.util.AncestryBuilder
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class IncrementTaskUseCaseTest {

    private val repo = mockk<TaskRepository>()
    private val incrementTaskUseCase = IncrementTaskUseCase(repo)

    @Test
    fun `increment counter - counter should increment`() = runBlocking {
        val ancestryList = listOf(1L, 2L, 3L, 4L, 5L)
        val ancestry = AncestryBuilder().withChildren(ancestryList).toString()
        val initialCounter = 3L
        val incrementedTask = TaskProvider.getGenericEntity()
            .copy(id = 6L, counter = initialCounter, ancestry = ancestry)
        coEvery { repo.getTask(incrementedTask.id) } returns incrementedTask
        val idsSlot = slot<List<Long>>()
        coEvery { repo.incrementTasks(capture(idsSlot)) } just Runs

        incrementTaskUseCase.execute(incrementedTask.id)

        Assert.assertEquals(ancestryList.plus(incrementedTask.id), idsSlot.captured)
    }
}