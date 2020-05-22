package com.aengussong.leisuretime

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.EmptyResultSetException
import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.util.CoroutineTestRule
import com.aengussong.leisuretime.util.TrampolineSchedulerRule
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest


class LeisureDataViewModelTest : KoinTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val trampolineSchedulerRule = TrampolineSchedulerRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val repo = mockk<LeisureRepository>()

    private val viewModel: LeisureDataViewModel = LeisureDataViewModel(repo)

    @Test
    fun `add item - item should be added`() {
        //setup
        val input = LeisureEntity(id = 1L, name = "fake")
        coEvery { repo.addLeisure(input) } just Runs
        //exercise
        viewModel.addLeisure(input)
        //verify
        coVerify { repo.addLeisure(input) }
    }

    @Test
    fun `add subitem - item should be added as subitem`(){
        val input = LeisureEntity(id=3L, name="fake", ancestry = "1/2")
    }

    @Test
    fun `edit leisure name - name should be edited`() {
        val id = 1234L
        val newName = "newName"

        repo.apply {
            every { updateLeisure(id, newName) } returns Completable.complete()
            every { getLeisure(id) } returns Single.just(LeisureEntity(name = newName))
        }

        viewModel.updateLeisure(id, newName)

        viewModel.getLeisure(id)
        val resultForNewName = viewModel.leisureLiveData.value
        Assert.assertNotNull(resultForNewName)
        Assert.assertEquals(newName, resultForNewName?.name)
    }

    @Test
    fun should_delete_item() {
        val leisureId = 1234L

        repo.apply {
            every { deleteLeisure(leisureId) } returns Completable.complete()
            every { getLeisure(leisureId) } returns emptyResultError()
        }

        viewModel.deleteLeisure(leisureId)

        viewModel.getLeisure(leisureId)
        val errorMsgForDeletedItem = viewModel.errorLiveData.value
        Assert.assertNotNull(errorMsgForDeletedItem)
    }

    private fun <T> emptyResultError(): Single<T> =
        Single.error(EmptyResultSetException("empty result set"))
}


