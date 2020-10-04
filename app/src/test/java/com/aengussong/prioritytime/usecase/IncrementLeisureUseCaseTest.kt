package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.LeisureRepository
import com.aengussong.prioritytime.testUtils.LeisureProvider
import com.aengussong.prioritytime.util.AncestryBuilder
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class IncrementLeisureUseCaseTest {

    private val repo = mockk<LeisureRepository>()
    private val incrementLeisureUseCase = IncrementLeisureUseCase(repo)

    @Test
    fun `increment counter - counter should increment`() = runBlocking {
        val ancestryList = listOf(1L, 2L, 3L, 4L, 5L)
        val ancestry = AncestryBuilder().withChildren(ancestryList).toString()
        val initialCounter = 3L
        val incrementedLeisure = LeisureProvider.getGenericEntity()
            .copy(id = 6L, counter = initialCounter, ancestry = ancestry)
        coEvery { repo.getLeisure(incrementedLeisure.id) } returns incrementedLeisure
        val idsSlot = slot<List<Long>>()
        coEvery { repo.incrementLeisures(capture(idsSlot)) } just Runs

        incrementLeisureUseCase.execute(incrementedLeisure.id)

        Assert.assertEquals(ancestryList.plus(incrementedLeisure.id), idsSlot.captured)
    }
}