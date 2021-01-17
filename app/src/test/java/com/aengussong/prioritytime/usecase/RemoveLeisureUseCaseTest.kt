package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.LeisureRepository
import com.aengussong.prioritytime.testUtils.LeisureProvider
import com.aengussong.prioritytime.util.AncestryBuilder
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RemoveLeisureUseCaseTest {

    private val repo = mockk<LeisureRepository>()
    private val useCase = RemoveLeisureUseCase(repo)

    @Test
    fun `remove leisure - should remove entity`() = runBlocking {
        val leisure = LeisureProvider.getGenericEntity()
            .copy(ancestry = AncestryBuilder().withChild(4).toString())
        coEvery { repo.removeLeisure(leisure.id) } just Runs
        coEvery { repo.removeLeisures(any()) } just Runs
        coEvery { repo.getAncestry(leisure.id) } returns leisure.ancestry

        useCase.execute(leisure.id)

        coVerify { repo.removeLeisure(leisure.id) }
        coVerify { repo.removeLeisures(any()) }
    }

    @Test
    fun `remove root leisure - only this root leisure and it's children should be deleted`() = runBlocking {
        val leisure =
            LeisureProvider.getGenericEntity().copy(ancestry = AncestryBuilder().toString())
        val leisureAncestryForChildren = AncestryBuilder(leisure.ancestry).withChild(leisure.id).toString()
        coEvery { repo.removeLeisure(leisure.id) } just Runs
        coEvery { repo.removeLeisures(any()) } just Runs
        coEvery { repo.getAncestry(leisure.id) } returns leisure.ancestry

        useCase.execute(leisure.id)

        coVerify(exactly = 1) { repo.removeLeisure(leisure.id) }
        coVerify(exactly = 0) { repo.removeLeisures(neq(leisureAncestryForChildren)) }
    }

    @Test
    fun `remove root leisure - root's children also should be removed`() = runBlocking {
        val parentId = 0L
        val parentAncestry = AncestryBuilder().withChild(parentId).toString()
        coEvery { repo.getAncestry(parentId) } returns AncestryBuilder().toString()
        coEvery { repo.removeLeisure(parentId) } just Runs
        coEvery { repo.removeLeisures(parentAncestry) } just Runs

        useCase.execute(parentId)

        coVerify { repo.removeLeisure(parentId) }
        coVerify { repo.removeLeisures(parentAncestry) }
    }

}