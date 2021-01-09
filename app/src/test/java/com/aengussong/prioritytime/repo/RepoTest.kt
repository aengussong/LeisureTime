package com.aengussong.prioritytime.repo

import com.aengussong.prioritytime.data.LeisureRepository
import com.aengussong.prioritytime.data.LeisureRepositoryImpl
import com.aengussong.prioritytime.data.local.SharedPrefs
import com.aengussong.prioritytime.data.local.dao.LeisureDao
import com.aengussong.prioritytime.data.local.entity.LeisureEntity
import com.aengussong.prioritytime.model.SortOrder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RepoTest {

    private val prefsMock = mockk<SharedPrefs>()
    private val localProviderMock = mockk<LeisureDao>()

    private val repo: LeisureRepository = LeisureRepositoryImpl(localProviderMock, prefsMock)

    @Test
    fun `get min leisure with linear sort - should fetch linear min element`() {
        every { prefsMock.getSortOrder() } returns SortOrder.LINEAR
        coEvery { localProviderMock.getMinLinear() } returns LeisureEntity("empty", 0, "empty")

        runBlocking {
            repo.getMinLeisure()
        }

        coVerify { localProviderMock.getMinLinear() }
    }

    @Test
    fun `get min leisure with hierarchial sort - should fetch hierarchial min element`() {
        every { prefsMock.getSortOrder() } returns SortOrder.HIERARCHY
        coEvery { localProviderMock.getMinHierarchial(any()) } returns LeisureEntity(
            "empty",
            0,
            "empty"
        )

        runBlocking {
            repo.getMinLeisure()
        }

        coVerify { localProviderMock.getMinHierarchial(any()) }
    }
}