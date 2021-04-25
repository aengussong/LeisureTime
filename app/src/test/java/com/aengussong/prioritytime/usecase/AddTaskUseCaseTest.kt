package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.TaskRepository
import com.aengussong.prioritytime.data.local.entity.TaskEntity
import com.aengussong.prioritytime.testUtils.CoroutineTestRule
import com.aengussong.prioritytime.util.AncestryBuilder
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

private const val FAKE_ID = 5L

internal class AddTaskUseCaseTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val repo = mockk<TaskRepository>()
    private val useCase = AddTaskUseCase(repo)

    @Test
    fun `add task without parent id - task should be created on root`() = runBlocking {
        val taskSlot = slot<TaskEntity>()
        val expected = mockTaskCreation(taskSlot, parentId = null)

        useCase.execute(expected.name)

        val result = taskSlot.captured
        assertTaskAncestry(expected, result)
    }

    @Test
    fun `add task - task entity should be added`() = runBlocking {
        val taskSlot = slot<TaskEntity>()
        val expected = mockTaskCreation(taskSlot, FAKE_ID)

        useCase.execute(expected.name, FAKE_ID)

        val result = taskSlot.captured
        assertTasksEqual(expected, result)
    }

    @Test
    fun `add task - should have counter as low as lowest on level`() = runBlocking {
        val ancestryBuilder = AncestryBuilder()
        val parentAncestry = ancestryBuilder.toString()
        val childAncestry = ancestryBuilder.withChild(FAKE_ID).toString()
        val lowestCounter = 3L
        coEvery { repo.getAncestry(FAKE_ID) } returns parentAncestry
        coEvery { repo.getLowestCounter(any()) } returns lowestCounter
        val entitySlot = slot<TaskEntity>()
        coEvery { repo.addTask(capture(entitySlot)) } returns 0L //added task id
        val expected = createTask("test", lowestCounter, childAncestry)

        useCase.execute(expected.name, FAKE_ID)

        val result = entitySlot.captured
        assertTaskCounters(expected, result)
        coVerify { repo.getLowestCounter(childAncestry) }
    }

    @Test
    fun `add first subtask - should have counter equal to parent`() = runBlocking {
        val parentId = FAKE_ID
        val parentCounter = 24L
        val ancestryBuilder = AncestryBuilder()
        val parentAncestry = ancestryBuilder.toString()
        val childAncestry = ancestryBuilder.withChild(parentId).toString()
        val lowestCounter = 0L
        coEvery { repo.getAncestry(parentId) } returns parentAncestry
        coEvery { repo.getLowestCounter(any()) } returns lowestCounter
        coEvery { repo.getTaskCounter(parentId) } returns parentCounter
        val entitySlot = slot<TaskEntity>()
        coEvery { repo.addTask(capture(entitySlot)) } returns 0L //added task id
        val expected = createTask("test", parentCounter, childAncestry)

        useCase.execute(expected.name, parentId)

        val result = entitySlot.captured
        assertTaskCounters(expected, result)
        coVerify { repo.getLowestCounter(childAncestry) }
    }

    private fun mockTaskCreation(
        taskSlot: CapturingSlot<TaskEntity>? = null,
        parentId: Long?,
        name: String = "fake"
    ): TaskEntity {
        val lowestCounter = 2L
        val ancestryBuilder = AncestryBuilder()
        coEvery { repo.getLowestCounter(any()) } returns lowestCounter
        coEvery { repo.getAncestry(any()) } returns ancestryBuilder.toString()
        coEvery {
            val addedTask = taskSlot?.let { capture(taskSlot) } ?: any()
            repo.addTask(addedTask)
        } returns 0L

        parentId?.let { ancestryBuilder.withChild(parentId) }

        return createTask(name, lowestCounter, ancestryBuilder.toString())
    }

    private fun createTask(name: String, counter: Long, ancestry: String) =
        TaskEntity(name = name, counter = counter, ancestry = ancestry)

    private fun assertTasksEqual(expected: TaskEntity, result: TaskEntity) {
        Assert.assertEquals(expected.name, result.name)
        assertTaskCounters(expected, result)
        assertTaskAncestry(expected, result)
    }

    private fun assertTaskCounters(expected: TaskEntity, result: TaskEntity) =
        Assert.assertEquals(expected.counter, result.counter)

    private fun assertTaskAncestry(expected: TaskEntity, result: TaskEntity) =
        Assert.assertEquals(expected.ancestry, result.ancestry)

}