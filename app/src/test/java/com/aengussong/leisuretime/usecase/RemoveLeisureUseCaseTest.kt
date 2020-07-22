package com.aengussong.leisuretime.usecase

import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.testUtils.LeisureProvider
import com.aengussong.leisuretime.util.AncestryBuilder
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
    fun `remove root leisure - only this root leisure should be deleted`() = runBlocking {
        val leisure =
            LeisureProvider.getGenericEntity().copy(ancestry = AncestryBuilder().toString())
        coEvery { repo.removeLeisure(leisure.id) } just Runs
        coEvery { repo.removeLeisures(any()) } just Runs
        coEvery { repo.getAncestry(leisure.id) } returns leisure.ancestry


        useCase.execute(leisure.id)

        coVerify(exactly = 1) { repo.removeLeisure(leisure.id) }
        coVerify(exactly = 0) { repo.removeLeisures(any()) }
    }

}