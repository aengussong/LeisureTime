package com.aengussong.leisuretime

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.EmptyResultSetException
import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.util.TrampolineSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest


class LeisureDataViewModelTest : KoinTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val trampolineSchedulerRule = TrampolineSchedulerRule()

    private val repo = mockk<LeisureRepository>()

    private val viewModel: LeisureDataViewModel = LeisureDataViewModel(repo)

    @Test
    fun `add item - item should be added`() {
        //setup
        val name = "leisure name"
        val input = LeisureEntity(name)

        repo.apply {
            every { addLeisure(input) } returns Completable.complete()
            every { getLeisure(name) } returns Single.just(input)
        }
        //exercise
        viewModel.addLeisure(input)
        //verify
        viewModel.getLeisure(name)
        val result = viewModel.leisureLiveData.value
        Assert.assertEquals(input, result)
    }

    @Test
    fun `edit leisure name - old name shouldn't be available`() {
        val oldName = "old"
        val newName = "new"

        repo.apply {
            every { updateLeisure(oldName, newName) } returns Completable.complete()
            every { getLeisure(newName) } returns Single.just(LeisureEntity(newName))
            every { getLeisure(oldName) } returns emptyResultError()
        }

        viewModel.updateLeisure(oldName, newName)

        viewModel.getLeisure(newName)
        val resultForNewName = viewModel.leisureLiveData.value
        Assert.assertNotNull(resultForNewName)

        viewModel.getLeisure(oldName)
        val errorMsgForOldName = viewModel.errorLiveData.value
        Assert.assertNotNull(errorMsgForOldName)
    }

    @Test
    fun should_delete_item() {
        val leisureName = "name"

        repo.apply {
            every { deleteLeisure(leisureName) } returns Completable.complete()
            every { getLeisure(leisureName) } returns emptyResultError()
        }

        viewModel.deleteLeisure(leisureName)

        viewModel.getLeisure(leisureName)
        val errorMsgForDeletedItem = viewModel.errorLiveData.value
        Assert.assertNotNull(errorMsgForDeletedItem)
    }

    @Test
    fun `update counter - counter should be updated`() {
        notImplemented()
    }

    @Test
    fun should_drop_all_counters() {
        notImplemented()
    }

    @Test
    fun should_notify_on_item_added() {
        notImplemented()
    }

    @Test
    fun should_notify_on_item_edited() {
        notImplemented()
    }

    @Test
    fun should_notify_on_item_deleted() {
        notImplemented()
    }

    @Test
    fun should_notify_on_all_counters_dropped() {
        notImplemented()
    }

    @Test
    fun newly_added_item_should_have_counter_as_small_as_smallest_counter_in_db() {
        notImplemented()
    }

    @Test
    fun `add item with already existing name - should do nothing`() {
        notImplemented()
    }

    @Test
    fun `start viewModel - data should be fetched`() {
        notImplemented()
    }

    private fun <T> emptyResultError():Single<T> = Single.error(EmptyResultSetException("empty result set"))

    private fun notImplemented() {
        Assert.fail("Not implemented")
    }
}


