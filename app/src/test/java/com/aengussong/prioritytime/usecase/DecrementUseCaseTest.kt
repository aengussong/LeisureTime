package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.TaskRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

internal class DecrementUseCaseTest {

    private val repo = mockk<TaskRepository>()
    private val useCase = DecrementUseCase(repo)

    @Test
    fun `decrement value - value should be decremented`() = runBlocking {
        val counter = 5L
        val testEntityId = 1L
        val counterCapture = slot<Long>()
        setUpTest(testEntityId, counter, counterCapture)

        useCase.execute(testEntityId)

        Assert.assertEquals(counter - 1, counterCapture.captured)
    }

    @Test
    fun `decrement counter - counter should not be dropped below zero`() = runBlocking {
        val counter = 0L
        val testEntityId = 2L
        val counterCapture = slot<Long>()
        setUpTest(testEntityId, counter, counterCapture)

        useCase.execute(testEntityId)

        coVerify(exactly = 0) { repo.setCounter(any(), any()) }
    }

    private fun setUpTest(id: Long, counter: Long, counterCapture: CapturingSlot<Long>) {
        coEvery { repo.getTaskCounter(id) } returns counter
        coEvery { repo.setCounter(id, capture(counterCapture)) } just Runs
    }
}