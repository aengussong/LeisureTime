package com.aengussong.prioritytime.viewModel

import com.aengussong.prioritytime.DbRelatedTest
import com.aengussong.prioritytime.LeisureDataViewModel
import com.aengussong.prioritytime.data.local.entity.LeisureEntity
import com.aengussong.prioritytime.model.Leisure
import com.aengussong.prioritytime.util.AncestryBuilder
import com.aengussong.prioritytime.util.Tree
import com.aengussong.prioritytime.util.getOrAwaitValue
import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Test
import org.koin.test.get
import org.koin.test.inject
import java.util.*
import kotlin.NoSuchElementException


@ExperimentalCoroutinesApi
class LeisureDataViewModelTest : DbRelatedTest() {

    private val viewModel: LeisureDataViewModel by inject()

    @Test
    fun startViewModel_dataShouldBeLoaded() = runBlocking {
        withContext(Dispatchers.IO) {
            val dbData = databaseManager.populateDatabase()
            val viewModel: LeisureDataViewModel = get()

            val result = viewModel.leisureLiveData.getOrAwaitValue()

            Assert.assertEquals(1, result.size)
            Assert.assertEquals(dbData.size, result.first().levels())
        }
    }

    @Test
    fun addItem_itemShouldBeAdded() = runBlocking {
        val leisureName = "addItemTest"

        viewModel.addLeisure(leisureName).join()

        val result = viewModel.leisureLiveData.getOrAwaitValue()
        Assert.assertEquals(leisureName, result.first().value.name)
    }

    @Test
    fun incrementCounter_counterShouldBeIncreased() = runBlocking {
        val dbData = databaseManager.populateDatabase()
        val testLeisure = dbData.last()
        val parentRootId = AncestryBuilder(testLeisure.ancestry).getRootParent()

        viewModel.incrementCounter(testLeisure.id).join()

        delay(500)
        val result = viewModel.leisureLiveData.getOrAwaitValue()

        val resultLeisure = result.findLeisure(parentRootId!!, testLeisure.id)
        Assert.assertEquals(testLeisure.counter + 1, resultLeisure?.counter)
    }

    @Test
    fun renameLeisure_leisureShouldBeRenamed() = runBlocking {
        val oldName = "oldName"
        val newName = "newName"
        viewModel.addLeisure(oldName).join()
        val leisureId = viewModel.leisureLiveData.getOrAwaitValue().first().value.id

        viewModel.renameLeisure(leisureId, newName).join()

        val updatedLeisureName = viewModel.leisureLiveData.getOrAwaitValue().first().value.name
        Assert.assertEquals(newName, updatedLeisureName)
    }


    @Test
    fun deleteLeisure_wholeBranchShouldBeDeleted() = runBlocking {
        val dbData = databaseManager.populateDatabase()
        val middleLevelEntity = databaseManager.lowestSecondLevel
        val topLevelEntity = databaseManager.lowestFirstLevel
        val preDeleteTree = viewModel.leisureLiveData.getOrAwaitValue().first()
        Assert.assertEquals(dbData.size, preDeleteTree.levels())

        viewModel.removeEntity(middleLevelEntity.id).join()

        val tree = viewModel.leisureLiveData.getOrAwaitValue().first()
        Assert.assertEquals(1, tree.levels())
        Assert.assertEquals(topLevelEntity.id, tree.value.id)
        Assert.assertEquals(0, tree.children().size)
    }

    @Test
    fun deleteLeisure_leisureSiblingsShouldNotBeDeleted() = runBlocking {
        val rootEntity = databaseManager.genericEntity.copy(id = 1)
        databaseManager.populateDatabase(rootEntity)
        val siblings = databaseManager.populateDatabaseWithChildren(rootEntity, 3)
        val entityToDelete = siblings.first()

        viewModel.removeEntity(entityToDelete.id).join()

        val tree = viewModel.leisureLiveData.getOrAwaitValue()
        val rootAncestryStack = AncestryBuilder(entityToDelete.ancestry).getAncestryStack()
        val siblingsAfterDelete = tree.findChildren(rootAncestryStack)
        Assert.assertEquals(siblings.size - 1, siblingsAfterDelete.size)
    }

    @Test
    fun dropCounters_allCountersShouldBeDrooped() = runBlocking {
        databaseManager.populateDatabase()
        viewModel.leisureLiveData.getOrAwaitValue().assertCounters(shouldBeZero = false)

        viewModel.dropCounters().join()

        viewModel.leisureLiveData.getOrAwaitValue().assertCounters(shouldBeZero = true)
    }

    @Test
    fun decrementCounter_counterShouldBeDecremented() = runBlocking {
        val counter = 5L
        val testEntity = genericLeisure().copy(counter = counter)
        databaseManager.populateDatabase(testEntity)

        viewModel.decrementLeisure(testEntity.id).join()

        val result = viewModel.leisureLiveData.getOrAwaitValue()
        val resultLeisure = result.findLeisure(testEntity.id)
        Assert.assertEquals(counter - 1, resultLeisure?.counter)
    }

    private fun List<Tree<Leisure>>.assertCounters(shouldBeZero: Boolean) {
        forEach { tree ->
            Assert.assertEquals(shouldBeZero, tree.value.counter == 0L)
            tree.children().assertCounters(shouldBeZero)
        }
    }

    private fun List<Tree<Leisure>>.findChildren(ancestryStack: Stack<Long>): List<Leisure> {
        if (ancestryStack.isEmpty()) {
            val children = mutableListOf<Leisure>()
            forEach {
                children.add(it.value)
            }
            return children
        }

        val ancestry = ancestryStack.pop()
        forEach {
            if (it.value.id == ancestry) {
                return@findChildren it.children().findChildren(ancestryStack)
            }
        }

        throw NoSuchElementException("Can't find entity with id $ancestry")
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

    private fun genericLeisure() = LeisureEntity(
        id = 1L,
        name = "generic",
        counter = 1L,
        updated = Date(),
        ancestry = AncestryBuilder().toString()
    )
}