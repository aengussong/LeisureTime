package com.aengussong.leisuretime.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aengussong.leisuretime.DbRelatedTest
import com.aengussong.leisuretime.LeisureDataViewModel
import com.aengussong.leisuretime.util.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.test.get
import org.koin.test.inject


class LeisureDataViewModelTest : DbRelatedTest() {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val viewModel: LeisureDataViewModel by inject()

    @Test
    fun startViewModel_dataShouldBeLoaded(): Unit = runBlocking {
        val dbData = databaseManager.populateDatabase()
        val viewModel: LeisureDataViewModel = get()

        val result = viewModel.leisureLiveData.getOrAwaitValue()

        Assert.assertEquals(1, result.size)
        Assert.assertEquals(dbData.size, result.first().levels())
    }

    @Test
    fun addItem_itemShouldBeAdded() = runBlocking {
        val leisureName = "addItemTest"

        val job = viewModel.addLeisure(leisureName)

        job.join()
        val result = viewModel.leisureLiveData.getOrAwaitValue()
        Assert.assertEquals(leisureName, result.first().value.name)
    }
}


