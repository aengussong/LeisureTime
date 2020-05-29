package com.aengussong.leisuretime.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aengussong.leisuretime.DbRelatedTest
import com.aengussong.leisuretime.LeisureDataViewModel
import com.aengussong.leisuretime.model.Leisure
import com.aengussong.leisuretime.util.AncestryBuilder
import com.aengussong.leisuretime.util.Tree
import com.aengussong.leisuretime.util.getOrAwaitValue
import kotlinx.coroutines.delay
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
    fun startViewModel_dataShouldBeLoaded() = runBlocking {
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

    @Test
    fun incrementCounter_counterShouldBeIncreased() = runBlocking {
        val dbData = databaseManager.populateDatabase()
        val testLeisure = dbData.last()
        val parentRootId = AncestryBuilder(testLeisure.ancestry).getRootParent()

        val job = viewModel.incrementCounter(testLeisure.id)
        job.join()

        delay(500)
        val result = viewModel.leisureLiveData.getOrAwaitValue()

        val resultLeisure = result.findLeisure(parentRootId!!, testLeisure.id)
        Assert.assertEquals(testLeisure.counter + 1, resultLeisure?.counter)
    }

    private fun List<Tree<Leisure>>.findLeisure(parentRootId: Long, leisureId: Long): Leisure? {
        forEach { tree ->
            if (tree.value.id == parentRootId) {
                return@findLeisure tree.children().findLeisure(leisureId)
            }
        }
        return null
    }

    private fun List<Tree<Leisure>>.findLeisure(leisureId: Long): Leisure? {
        forEach { tree ->
            return@findLeisure if (tree.value.id == leisureId)
                tree.value
            else
                tree.children().findLeisure(leisureId)
        }
        return null
    }
}


