package com.aengussong.prioritytime.repo

import com.aengussong.prioritytime.data.TaskRepository
import com.aengussong.prioritytime.data.TaskRepositoryImpl
import com.aengussong.prioritytime.data.local.SharedPrefs
import com.aengussong.prioritytime.data.local.dao.TasksDao
import com.aengussong.prioritytime.data.local.entity.TaskEntity
import com.aengussong.prioritytime.model.SortOrder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RepoTest {

    private val prefsMock = mockk<SharedPrefs>()
    private val localProviderMock = mockk<TasksDao>()

    private val repo: TaskRepository = TaskRepositoryImpl(localProviderMock, prefsMock)

    @Test
    fun `get min task with linear sort - should fetch linear min element`() {
        every { prefsMock.getSortOrder() } returns SortOrder.LINEAR
        coEvery { localProviderMock.getMinLinear() } returns TaskEntity("empty", 0, "empty")

        runBlocking {
            repo.getMinTask()
        }

        coVerify { localProviderMock.getMinLinear() }
    }

    @Test
    fun `get min task with hierarchial sort - should fetch hierarchial min element`() {
        every { prefsMock.getSortOrder() } returns SortOrder.HIERARCHY
        coEvery { localProviderMock.getMinHierarchial(any()) } returns TaskEntity(
            "empty",
            0,
            "empty"
        )

        runBlocking {
            repo.getMinTask()
        }

        coVerify { localProviderMock.getMinHierarchial(any()) }
    }
}