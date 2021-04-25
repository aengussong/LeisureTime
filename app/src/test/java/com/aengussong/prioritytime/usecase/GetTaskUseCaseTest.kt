package com.aengussong.prioritytime.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aengussong.prioritytime.data.TaskRepository
import com.aengussong.prioritytime.data.local.entity.TaskEntity
import com.aengussong.prioritytime.testUtils.TaskProvider
import com.aengussong.prioritytime.util.AncestryBuilder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.*

class GetTaskUseCaseTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val repo = mockk<TaskRepository>()

    private val getTaskUseCase = GetTaskUseCase(repo)

    @Test
    fun `get hierarchial data with three levels - should be converted to tree hierarchy`() = runBlocking {
        val ancestryBuilder = AncestryBuilder()

        val firstLevelAncestry = ancestryBuilder.toString()
        val firstLevelId = 2L
        val firstLevelName = "firstLevel"

        val secondLevelAncestry = ancestryBuilder.withChild(firstLevelId).toString()
        val secondLevelId = 3L
        val secondLevelName = "secondLevel"

        val thirdLevelAncestry = ancestryBuilder.withChild(secondLevelId).toString()
        val thirdLevelId = 4L
        val thirdLevelName = "thirdLevel"

        val first = TaskEntity(firstLevelId, firstLevelName, 2, Date(), firstLevelAncestry)
        val second = TaskEntity(secondLevelId, secondLevelName, 2, Date(), secondLevelAncestry)
        val third = TaskEntity(thirdLevelId, thirdLevelName, 2, Date(), thirdLevelAncestry)

        every { repo.getHierarchialTasks() } returns flowOf(
            listOf(
                first,
                second,
                third
            )
        )

        val resultTree = getTaskUseCase.getHierarchialTasks().first()

        assertEquals(1, resultTree.size)

        val firstLevelItem = resultTree.first()
        assertEquals(3, firstLevelItem.levels())
        assertEquals(firstLevelName, firstLevelItem.value.name)
        assertEquals(1, firstLevelItem.childrenCount())

        val secondLevelItem = firstLevelItem.children().first()
        assertEquals(2, secondLevelItem.levels())
        assertEquals(secondLevelName, secondLevelItem.value.name)
        assertEquals(1, firstLevelItem.childrenCount())

        val thirdLevelItem = secondLevelItem.children().first()
        assertEquals(1, thirdLevelItem.levels())
        assertEquals(thirdLevelName, thirdLevelItem.value.name)
        assertEquals(0, thirdLevelItem.childrenCount())
    }

    @Test
    fun `get hierarchial data with several root tasks - should return several unconnected trees`() =
        runBlocking {
            val rootAncestry = AncestryBuilder()

            val firstLevelAncestry = rootAncestry.toString()

            val parentId = 2L
            val secondLevelAncestry = rootAncestry.withChild(parentId).toString()

            val first = TaskEntity(1L, "first", 2, Date(), firstLevelAncestry)
            val second = TaskEntity(parentId, "second", 2, Date(), firstLevelAncestry)
            val third = TaskEntity(3L, "third", 2, Date(), secondLevelAncestry)

            every { repo.getHierarchialTasks() } returns flowOf(
                listOf(
                    first,
                    second,
                    third
                )
            )

            val resultTree = getTaskUseCase.getHierarchialTasks().first()

            assertEquals(2, resultTree.size)
            assertEquals(1, resultTree.first().levels())
            assertEquals(2, resultTree[1].levels())
        }

    @Test
    fun `get hierarchial data - root trees should be ordered by counter and updated date`() = runBlocking {
        val lp = TaskProvider()
        val recentlyUpdated = lp.getRecentlyUpdatedEntity(true)
        val largestCounter = lp.getLargestCounterEntity()
        val longAgoUpdated = lp.getLongAgoUpdatedEntity(true)
        val entities = listOf(recentlyUpdated, largestCounter, longAgoUpdated)
        every { repo.getHierarchialTasks() } returns flowOf(entities)

        val resultTree = getTaskUseCase.getHierarchialTasks().first()

        //assert order
        assertEquals(lp.id_longAgoUpdated, resultTree[0].value.id)
        assertEquals(lp.id_recentlyUpdated, resultTree[1].value.id)
        assertEquals(lp.id_largestCounter, resultTree[2].value.id)
    }

    @Test
    fun `get hierarchial trees - subtrees should be ordered by counter and updated date`() = runBlocking {
        val lp = TaskProvider()

        val parentId = 1L
        val childAncestry = AncestryBuilder().withChild(parentId).toString()

        val parentEntity = TaskEntity(parentId, "parent", 0, Date(), lp.rootAncestry)
        val largestChild = lp.getLargestCounterEntity(childAncestry)
        val equalRecentChild = lp.getRecentlyUpdatedEntity(true, childAncestry)
        val equalLongAgoChild = lp.getLongAgoUpdatedEntity(true, childAncestry)
        val entities = listOf(parentEntity, equalRecentChild, largestChild, equalLongAgoChild)

        every { repo.getHierarchialTasks() } returns flowOf(entities)

        val resultTree = getTaskUseCase.getHierarchialTasks().first()

        assertEquals(1, resultTree.size)
        val resultChildren = resultTree.first().children()
        //assert order
        assertEquals(lp.id_longAgoUpdated, resultChildren[0].value.id)
        assertEquals(lp.id_recentlyUpdated, resultChildren[1].value.id)
        assertEquals(lp.id_largestCounter, resultChildren[2].value.id)
    }

    @Test
    fun `get linearly sorted tasks - tasks should be ordered by counter and updated date`() = runBlocking {
        val lp = TaskProvider()
        val parentId = 1L
        val childAncestry = AncestryBuilder().withChild(parentId).toString()
        val parentEntitySmallestCounter = TaskEntity(parentId, "parent", 0, Date(), lp.rootAncestry)
        val largestChild = lp.getLargestCounterEntity(childAncestry)
        val equalRecentChild = lp.getRecentlyUpdatedEntity(true, childAncestry)
        val equalLongAgoChild = lp.getLongAgoUpdatedEntity(true, childAncestry)
        val entities = listOf(parentEntitySmallestCounter, equalRecentChild, largestChild, equalLongAgoChild)
        every { repo.getLinearTasks() } returns flowOf(entities)

        val resultTree = getTaskUseCase.getLinearTasks().first()

        assertEquals(4, resultTree.size)
        //assert order
        assertEquals(parentEntitySmallestCounter.id, resultTree[0].value.id)
        assertEquals(lp.id_longAgoUpdated, resultTree[1].value.id)
        assertEquals(lp.id_recentlyUpdated, resultTree[2].value.id)
        assertEquals(lp.id_largestCounter, resultTree[3].value.id)
    }
}