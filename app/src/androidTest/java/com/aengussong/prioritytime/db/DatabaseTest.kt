package com.aengussong.prioritytime.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aengussong.prioritytime.DbRelatedTest
import com.aengussong.prioritytime.data.local.dao.TasksDao
import com.aengussong.prioritytime.data.local.entity.TaskEntity
import com.aengussong.prioritytime.util.AncestryBuilder
import com.aengussong.prioritytime.util.ROOT_ANCESTRY
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.inject

class DatabaseTest : DbRelatedTest() {

    private val tasksDao: TasksDao by inject()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Test
    fun getLowestCounter_shouldReturnLowestCounterForAncestry() = runBlocking {
        val parentEntity = databaseManager.genericEntity.copy(id = 2L)
        val parentSibling = databaseManager.genericEntity.copy(id = 3L, counter = 60L)
        val childrenAncestry =
            AncestryBuilder(parentEntity.ancestry).withChild(parentEntity.id).toString()
        val childSibling =
            databaseManager.genericEntity.copy(id = 5L, counter = 5L, ancestry = childrenAncestry)
        val lowestChildSibling =
            databaseManager.genericEntity.copy(id = 6L, counter = 1L, ancestry = childrenAncestry)
        databaseManager.populateDatabase(
            parentEntity,
            parentSibling,
            childSibling,
            lowestChildSibling
        )

        val result = tasksDao.getLowestCounter(childrenAncestry)

        Assert.assertEquals(lowestChildSibling.counter, result)
    }

    @Test
    fun getLowestCounterForEmptyParent_shouldReturnZero() = runBlocking {
        val parentEntity = databaseManager.genericEntity.copy(id = 2L)
        val parentSibling = databaseManager.genericEntity.copy(id = 3L, counter = 60L)
        val childrenAncestry =
            AncestryBuilder(parentEntity.ancestry).withChild(parentEntity.id).toString()
        databaseManager.populateDatabase(parentEntity, parentSibling)

        val result = tasksDao.getLowestCounter(childrenAncestry)

        Assert.assertEquals(0, result)
    }

    @Test
    fun getLowestCounter_returnsLowestCounterForCurrentLevel() = runBlocking {
        databaseManager.populateDatabase()
        val entityWithLowestCounterOnSecondLevel = databaseManager.lowestSecondLevel
        val secondLevelAncestry = entityWithLowestCounterOnSecondLevel.ancestry

        val result = tasksDao.getLowestCounter(secondLevelAncestry)

        Assert.assertEquals(entityWithLowestCounterOnSecondLevel.counter, result)
    }

    @Test
    fun getTasks_shouldReturnOrderedByAncestry() = runBlocking {
        databaseManager.populateDatabase()
        val expected = databaseManager.getOrderedByAncestry()

        val result: List<TaskEntity> = tasksDao.getHierarchialTasks().first()

        Assert.assertEquals(expected, result)
    }

    @Test
    fun incrementTaskCounter_counterShouldBeIncremented() = runBlocking {
        databaseManager.populateDatabase()
        val testEntity = databaseManager.lowestSecondLevel
        val parentEntity = databaseManager.lowestFirstLevel

        tasksDao.incrementTasks(listOf(testEntity.id, parentEntity.id))

        val resultTestEntity = tasksDao.getTask(testEntity.id)
        val resultParentEntity = tasksDao.getTask(parentEntity.id)
        Assert.assertEquals(testEntity.counter + 1, resultTestEntity.counter)
        Assert.assertEquals(parentEntity.counter + 1, resultParentEntity.counter)
    }

    @Test
    fun incrementTaskCounter_updatedDateShouldUpdate() = runBlocking {
        databaseManager.populateDatabase()
        val testEntity = databaseManager.lowestSecondLevel
        val preIncrementEntity = tasksDao.getTask(testEntity.id)
        Assert.assertEquals(testEntity.updated, preIncrementEntity.updated)

        tasksDao.incrementTasks(listOf(testEntity.id))

        val resultEntity = tasksDao.getTask(testEntity.id)

        Assert.assertNotEquals(testEntity.updated, resultEntity.updated)
    }

    @Test
    fun removeTaskByAncestry_taskSubTreeShouldBeRemoved() = runBlocking {
        databaseManager.populateDatabase()
        val removedEntity = databaseManager.lowestSecondLevel
        val parentEntity = databaseManager.lowestFirstLevel
        val preDelete: List<TaskEntity> = tasksDao.getHierarchialTasks().first()
        Assert.assertEquals(3, preDelete.size)

        tasksDao.removeTasks(removedEntity.ancestry)

        val postDelete: List<TaskEntity> = tasksDao.getHierarchialTasks().first()

        Assert.assertEquals(1, postDelete.size)
        Assert.assertEquals(parentEntity.id, postDelete.first().id)
    }

    @Test
    fun getMinHierarchialTask_shouldReturnMinRootTask() = runBlocking {
        val highCounterTask =
            databaseManager.genericEntity.copy(id = 1, counter = 10, ancestry = ROOT_ANCESTRY)
        val lowestCounterTask =
            databaseManager.genericEntity.copy(id = 2, counter = 1, ancestry = ROOT_ANCESTRY)
        databaseManager.populateDatabase(highCounterTask, lowestCounterTask)
        databaseManager.populateDatabaseWithChildren(highCounterTask, 10)

        val minEntity = tasksDao.getMinHierarchial(ROOT_ANCESTRY)

        Assert.assertEquals(lowestCounterTask.id, minEntity?.id)
    }

    @Test
    fun getMinLinearTask_shouldReturnTaskWithMinCounter() = runBlocking {
        val parent = databaseManager.genericEntity.copy(id = 1L, counter = 10L)
        val child = databaseManager.genericEntity.copy(
            id = 2L,
            counter = 5L,
            ancestry = parent.ancestryForChildren()
        )
        val grandchild = databaseManager.genericEntity.copy(
            id = 3L,
            counter = 0L,
            ancestry = child.ancestryForChildren()
        )
        val anotherParent = databaseManager.genericEntity.copy(id = 4L, counter = 1L)
        databaseManager.populateDatabase(parent, child, grandchild, anotherParent)

        val minEntity = tasksDao.getMinLinear()

        Assert.assertEquals(grandchild.id, minEntity?.id)
    }

    private fun TaskEntity.ancestryForChildren() =
        AncestryBuilder(ancestry).withChild(id).toString()

}