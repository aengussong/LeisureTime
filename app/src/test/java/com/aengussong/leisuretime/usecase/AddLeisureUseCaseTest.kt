package com.aengussong.leisuretime.usecase

import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.testUtils.CoroutineTestRule
import com.aengussong.leisuretime.util.AncestryBuilder
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
        val ancestryBuilder = AncestryBuilder()
        val parentAncestry = ancestryBuilder.toString()
        val childAncestry = ancestryBuilder.withChild(FAKE_PARENT_ID).toString()
        val lowestCounter = 3L
        coEvery { repo.getAncestry(FAKE_PARENT_ID) } returns parentAncestry
        coEvery { repo.getLowestCounter(any()) } returns lowestCounter
        val entitySlot = slot<LeisureEntity>()
        coEvery { repo.addLeisure(capture(entitySlot)) } returns 0L
        val expected = createLeisure("test", lowestCounter, childAncestry)

        useCase.execute(expected.name, FAKE_PARENT_ID)

        val result = entitySlot.captured
        assertLeisureCounters(expected, result)
        coVerify { repo.getLowestCounter(childAncestry) }
    }

    private fun mockLeisureCreation(
        leisureSlot: CapturingSlot<LeisureEntity>? = null,
        parentId: Long?,
        name: String = "fake"
    ): LeisureEntity {
        val lowestCounter = 2L
        val ancestryBuilder = AncestryBuilder()
        coEvery { repo.getLowestCounter(any()) } returns lowestCounter
        coEvery { repo.getAncestry(any()) } returns ancestryBuilder.toString()
        coEvery {
            val addedLeisure = leisureSlot?.let { capture(leisureSlot) } ?: any()
            repo.addLeisure(addedLeisure)
        } returns 0L

        parentId?.let { ancestryBuilder.withChild(parentId) }

        return createLeisure(name, lowestCounter, ancestryBuilder.toString())
    }

    private fun createLeisure(name: String, counter: Long, ancestry: String) =
        LeisureEntity(name = name, counter = counter, ancestry = ancestry)

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