package com.aengussong.leisuretime

import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.di.dataModule
import com.aengussong.leisuretime.util.mockDbModule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule

@RunWith(JUnit4::class)
class LeisureDataViewModelTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            mockDbModule,
            dataModule
        )
    }

    private val viewModel: LeisureDataViewModel = LeisureDataViewModel()

    @Test
    fun should_add_item() {
        val name = "leisure name"
        val input = LeisureEntity(name)

        viewModel.addLeisure(input)

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

    private fun notImplemented() {
        Assert.fail("Not implemented")
    }
}


