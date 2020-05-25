package com.aengussong.leisuretime.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aengussong.leisuretime.LeisureDataViewModel
import com.aengussong.leisuretime.util.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject


class LeisureDataViewModelTest : KoinTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val viewModel: LeisureDataViewModel by inject()

    @Test
    fun addItem_itemShouldBeAdded() = runBlocking {
        val leisureName = "fake"

        viewModel.addLeisure(leisureName)

        val result = viewModel.leisureLiveData.getOrAwaitValue()
        Assert.assertEquals(leisureName, result.first().name)
    }
}


