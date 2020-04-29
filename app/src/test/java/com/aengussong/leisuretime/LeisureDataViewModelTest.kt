package com.aengussong.leisuretime

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
    fun should_edit_item() {
        notImplemented()
    }

    @Test
    fun should_delete_item() {
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

    private fun notImplemented() {
        Assert.fail("Not implemented")
    }
}


