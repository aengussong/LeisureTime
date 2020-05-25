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


internal class AddLeisureUseCaseTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val repo = mockk<LeisureRepository>()
    private val useCase = AddLeisureUseCase(repo)

    @Test
    fun `add leisure without parent id - leisure should be created on root`() = runBlocking {
        val leisureSlot = slot<LeisureEntity>()
        val expected = mockLeisureCreation(null, leisureSlot)

        useCase.execute(expected.name)

        val result = leisureSlot.captured
        assertLeisuresEqual(expected, result)
    }

    @Test
    fun `add leisure - leisure entity should be added`() = runBlocking {
        val parentId = 5L
        val leisureSlot = slot<LeisureEntity>()
        val expected = mockLeisureCreation(parentId, leisureSlot)

        useCase.execute(expected.name, parentId)

        val result = leisureSlot.captured
        assertLeisuresEqual(expected, result)
    }

    private fun mockLeisureCreation(
        parentId: Long?,
        leisureSlot: CapturingSlot<LeisureEntity>
    ): LeisureEntity {
        val lowestCounter = 4L
        val parentAncestry = "2/4"

        coEvery { repo.getLowestCounter(any()) } returns lowestCounter
        coEvery { repo.getAncestry(any()) } returns parentAncestry
        coEvery { repo.addLeisure(capture(leisureSlot)) } just Runs

        return createLeisure("fake", lowestCounter, parentId?.let{ "$parentAncestry/$parentId" })
    }

    private fun createLeisure(name: String, counter: Long, ancestry: String?) =
        LeisureEntity(name = name, counter = counter, ancestry = ancestry ?: ROOT_ANCESTRY)


    private fun assertLeisuresEqual(expected: LeisureEntity, result: LeisureEntity) {
        Assert.assertEquals(expected.name, result.name)
        Assert.assertEquals(expected.counter, result.counter)
        Assert.assertEquals(expected.ancestry, result.ancestry)
    }

}