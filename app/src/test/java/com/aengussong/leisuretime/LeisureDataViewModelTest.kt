package com.aengussong.leisuretime

import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import org.junit.Assert
import org.junit.Test

class LeisureDataViewModelTest {

    private val viewModel: LeisureDataViewModel = LeisureDataViewModel()

    @Test
    fun `should add item`() {
        val name = "leisure name"
        val input = LeisureEntity(name)

        viewModel.addLeisure(input)

        val result = viewModel.getLeisure(name)
        Assert.assertEquals(input, result)
    }

    @Test
    fun `should edit item`() {
        notImplemented()
    }

    @Test
    fun `should delete item`() {
        notImplemented()
    }

    @Test
    fun `should drop all counters`() {
        notImplemented()
    }

    @Test
    fun `should notify on item added`(){

    }

    @Test
    fun `should notify on item edited`(){

    }

    @Test
    fun `should notify on item deleted`(){

    }

    @Test
    fun `should notify on all counters dropped`(){

    }

    @Test
    fun `newly added item should have counter as small as smallest counter in db`() {
        notImplemented()
    }

    private fun notImplemented() {
        Assert.fail("Not implemented")
    }
}


