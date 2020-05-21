package com.aengussong.leisuretime

import com.aengussong.leisuretime.util.mockDbModule
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin

class DatabaseTest {

    @Before
    fun setUp() {
        loadKoinModules(mockDbModule)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun addItem_itemShouldBeAdded() {
        notImplemented()
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
    fun addLeisure_shouldHaveCounterAsSmallAsSmallestCounterInDb() {
        notImplemented()
    }

    private fun notImplemented() {
        Assert.fail("Not implemented")
    }
}