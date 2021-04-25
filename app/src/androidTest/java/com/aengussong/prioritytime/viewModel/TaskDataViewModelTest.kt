package com.aengussong.prioritytime.viewModel

import com.aengussong.prioritytime.DbRelatedTest
import com.aengussong.prioritytime.TaskDataViewModel
import com.aengussong.prioritytime.data.local.entity.TaskEntity
import com.aengussong.prioritytime.model.Task
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
class TaskDataViewModelTest : DbRelatedTest() {

    private val viewModel: TaskDataViewModel by inject()

    @Test
    fun startViewModel_dataShouldBeLoaded() = runBlocking {
        withContext(Dispatchers.IO) {
            val dbData = databaseManager.populateDatabase()
            val viewModel: TaskDataViewModel = get()

            val result = viewModel.taskLiveData.getOrAwaitValue()

            Assert.assertEquals(1, result.size)
            Assert.assertEquals(dbData.size, result.first().levels())
        }
    }

    @Test
    fun addItem_itemShouldBeAdded() = runBlocking {
        val taskName = "addItemTest"

        viewModel.addTask(taskName).join()

        val result = viewModel.taskLiveData.getOrAwaitValue()
        Assert.assertEquals(taskName, result.first().value.name)
    }

    @Test
    fun incrementCounter_counterShouldBeIncreased() = runBlocking {
        val dbData = databaseManager.populateDatabase()
        val testTask = dbData.last()
        val parentRootId = AncestryBuilder(testTask.ancestry).getRootParent()

        viewModel.incrementCounter(testTask.id).join()

        delay(500)
        val result = viewModel.taskLiveData.getOrAwaitValue()

        val resultTask = result.findTask(parentRootId!!, testTask.id)
        Assert.assertEquals(testTask.counter + 1, resultTask?.counter)
    }

    @Test
    fun renameTask_taskShouldBeRenamed() = runBlocking {
        val oldName = "oldName"
        val newName = "newName"
        viewModel.addTask(oldName).join()
        val taskId = viewModel.taskLiveData.getOrAwaitValue().first().value.id

        viewModel.renameTask(taskId, newName).join()

        val updatedTaskName = viewModel.taskLiveData.getOrAwaitValue().first().value.name
        Assert.assertEquals(newName, updatedTaskName)
    }


    @Test
    fun deleteTask_wholeBranchShouldBeDeleted() = runBlocking {
        val dbData = databaseManager.populateDatabase()
        val middleLevelEntity = databaseManager.lowestSecondLevel
        val topLevelEntity = databaseManager.lowestFirstLevel
        val preDeleteTree = viewModel.taskLiveData.getOrAwaitValue().first()
        Assert.assertEquals(dbData.size, preDeleteTree.levels())

        viewModel.removeEntity(middleLevelEntity.id).join()

        val tree = viewModel.taskLiveData.getOrAwaitValue().first()
        Assert.assertEquals(1, tree.levels())
        Assert.assertEquals(topLevelEntity.id, tree.value.id)
        Assert.assertEquals(0, tree.children().size)
    }

    @Test
    fun deleteTask_taskSiblingsShouldNotBeDeleted() = runBlocking {
        val rootEntity = databaseManager.genericEntity.copy(id = 1)
        databaseManager.populateDatabase(rootEntity)
        val siblings = databaseManager.populateDatabaseWithChildren(rootEntity, 3)
        val entityToDelete = siblings.first()

        viewModel.removeEntity(entityToDelete.id).join()

        val tree = viewModel.taskLiveData.getOrAwaitValue()
        val rootAncestryStack = AncestryBuilder(entityToDelete.ancestry).getAncestryStack()
        val siblingsAfterDelete = tree.findChildren(rootAncestryStack)
        Assert.assertEquals(siblings.size - 1, siblingsAfterDelete.size)
    }

    @Test
    fun dropCounters_allCountersShouldBeDrooped() = runBlocking {
        databaseManager.populateDatabase()
        viewModel.taskLiveData.getOrAwaitValue().assertCounters(shouldBeZero = false)

        viewModel.dropCounters().join()

        viewModel.taskLiveData.getOrAwaitValue().assertCounters(shouldBeZero = true)
    }

    @Test
    fun decrementCounter_counterShouldBeDecremented() = runBlocking {
        val counter = 5L
        val testEntity = genericTask().copy(counter = counter)
        databaseManager.populateDatabase(testEntity)

        viewModel.decrementTask(testEntity.id).join()

        val result = viewModel.taskLiveData.getOrAwaitValue()
        val resultTask = result.findTask(testEntity.id)
        Assert.assertEquals(counter - 1, resultTask?.counter)
    }

    private fun List<Tree<Task>>.assertCounters(shouldBeZero: Boolean) {
        forEach { tree ->
            Assert.assertEquals(shouldBeZero, tree.value.counter == 0L)
            tree.children().assertCounters(shouldBeZero)
        }
    }

    private fun List<Tree<Task>>.findChildren(ancestryStack: Stack<Long>): List<Task> {
        if (ancestryStack.isEmpty()) {
            val children = mutableListOf<Task>()
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

    private fun List<Tree<Task>>.findTask(parentRootId: Long, taskId: Long): Task? {
        forEach { tree ->
            if (tree.value.id == parentRootId) {
                return@findTask tree.children().findTask(taskId)
            }
        }
        return null
    }

    private fun List<Tree<Task>>.findTask(taskId: Long): Task? {
        forEach { tree ->
            return@findTask if (tree.value.id == taskId)
                tree.value
            else
                tree.children().findTask(taskId)
        }
        return null
    }

    private fun genericTask() = TaskEntity(
        id = 1L,
        name = "generic",
        counter = 1L,
        updated = Date(),
        ancestry = AncestryBuilder().toString()
    )
}