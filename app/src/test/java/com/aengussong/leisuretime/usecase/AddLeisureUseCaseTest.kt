package com.aengussong.leisuretime.usecase

import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.util.CoroutineTestRule
import com.aengussong.leisuretime.util.ROOT_ANCESTRY
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

private const val FAKE_PARENT_ID = 5L

internal class AddLeisureUseCaseTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val repo = mockk<LeisureRepository>()
    private val useCase = AddLeisureUseCase(repo)

    @Test
    fun `add leisure without parent id - leisure should be created on root`() = runBlocking {
        val leisureSlot = slot<LeisureEntity>()
        val expected = mockLeisureCreation(leisureSlot, parentId = null)

        useCase.execute(expected.name)

        val result = leisureSlot.captured
        assertLeisureAncestry(expected, result)
    }

    @Test
    fun `add leisure - leisure entity should be added`() = runBlocking {
        val leisureSlot = slot<LeisureEntity>()
        val expected = mockLeisureCreation(leisureSlot, FAKE_PARENT_ID)

        useCase.execute(expected.name, FAKE_PARENT_ID)

        val result = leisureSlot.captured
        assertLeisuresEqual(expected, result)
    }

    @Test
    fun `add leisure - should have counter as low as lowest on level`() = runBlocking {
        val lowestCounter = 2L
        val leisureSlot = slot<LeisureEntity>()
        val expected = mockLeisureCreation(leisureSlot, FAKE_PARENT_ID, lowestCounter)

        useCase.execute(expected.name, FAKE_PARENT_ID)

        val result = leisureSlot.captured
        assertLeisureCounters(expected, result)
    }

    @Test(expected = CyclingReferenceException::class)
    fun `add leisure with cycling parent reference - should throw cycling exception`() =
        runBlocking {
            val parentId = 2L
            val parentAncestry = "1/2/3"
            mockLeisureCreation(parentId = parentId, parentAncestry = parentAncestry)

            useCase.execute("_", parentId)
        }

    private fun mockLeisureCreation(
        leisureSlot: CapturingSlot<LeisureEntity>? = null,
        parentId: Long?,
        lowestCounter: Long = 4L,
        parentAncestry: String = "2/4"
    ): LeisureEntity {
        coEvery { repo.getLowestCounter(any()) } returns lowestCounter
        coEvery { repo.getAncestry(any()) } returns parentAncestry
        coEvery {
            val addedLeisure = leisureSlot?.let { capture(leisureSlot) } ?: any()
            repo.addLeisure(addedLeisure)
        } just Runs

        return createLeisure("fake", lowestCounter, parentId?.let { "$parentAncestry/$parentId" })
    }

    private fun createLeisure(name: String, counter: Long, ancestry: String?) =
        LeisureEntity(name = name, counter = counter, ancestry = ancestry ?: ROOT_ANCESTRY)

    private fun assertLeisuresEqual(expected: LeisureEntity, result: LeisureEntity) {
        Assert.assertEquals(expected.name, result.name)
        assertLeisureCounters(expected, result)
        assertLeisureAncestry(expected, result)
    }

    private fun assertLeisureCounters(expected: LeisureEntity, result: LeisureEntity) =
        Assert.assertEquals(expected.counter, result.counter)

    private fun assertLeisureAncestry(expected: LeisureEntity, result: LeisureEntity) =
        Assert.assertEquals(expected.ancestry, result.ancestry)

}