package com.aengussong.prioritytime.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aengussong.prioritytime.data.LeisureRepository
import com.aengussong.prioritytime.data.local.entity.LeisureEntity
import com.aengussong.prioritytime.testUtils.LeisureProvider
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

class GetLeisureUseCaseTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val repo = mockk<LeisureRepository>()

    private val getLeisureUseCase = GetLeisureUseCase(repo)

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

        val first = LeisureEntity(firstLevelId, firstLevelName, 2, Date(), firstLevelAncestry)
        val second = LeisureEntity(secondLevelId, secondLevelName, 2, Date(), secondLevelAncestry)
        val third = LeisureEntity(thirdLevelId, thirdLevelName, 2, Date(), thirdLevelAncestry)

        every { repo.getHierarchialLeisures() } returns flowOf(
            listOf(
                first,
                second,
                third
            )
        )

        val resultTree = getLeisureUseCase.getHierarchialLeisures().first()

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
    fun `get hierarchial data with several root leisures - should return several unconnected trees`() =
        runBlocking {
            val rootAncestry = AncestryBuilder()

            val firstLevelAncestry = rootAncestry.toString()

            val parentId = 2L
            val secondLevelAncestry = rootAncestry.withChild(parentId).toString()

            val first = LeisureEntity(1L, "first", 2, Date(), firstLevelAncestry)
            val second = LeisureEntity(parentId, "second", 2, Date(), firstLevelAncestry)
            val third = LeisureEntity(3L, "third", 2, Date(), secondLevelAncestry)

            every { repo.getHierarchialLeisures() } returns flowOf(
                listOf(
                    first,
                    second,
                    third
                )
            )

            val resultTree = getLeisureUseCase.getHierarchialLeisures().first()

            assertEquals(2, resultTree.size)
            assertEquals(1, resultTree.first().levels())
            assertEquals(2, resultTree[1].levels())
        }

    @Test
    fun `get hierarchial data - root trees should be ordered by counter and updated date`() = runBlocking {
        val lp = LeisureProvider()
        val recentlyUpdated = lp.getRecentlyUpdatedEntity(true)
        val largestCounter = lp.getLargestCounterEntity()
        val longAgoUpdated = lp.getLongAgoUpdatedEntity(true)
        val entities = listOf(recentlyUpdated, largestCounter, longAgoUpdated)
        every { repo.getHierarchialLeisures() } returns flowOf(entities)

        val resultTree = getLeisureUseCase.getHierarchialLeisures().first()

        //assert order
        assertEquals(lp.id_longAgoUpdated, resultTree[0].value.id)
        assertEquals(lp.id_recentlyUpdated, resultTree[1].value.id)
        assertEquals(lp.id_largestCounter, resultTree[2].value.id)
    }

    @Test
    fun `get hierarchial trees - subtrees should be ordered by counter and updated date`() = runBlocking {
        val lp = LeisureProvider()

        val parentId = 1L
        val childAncestry = AncestryBuilder().withChild(parentId).toString()

        val parentEntity = LeisureEntity(parentId, "parent", 0, Date(), lp.rootAncestry)
        val largestChild = lp.getLargestCounterEntity(childAncestry)
        val equalRecentChild = lp.getRecentlyUpdatedEntity(true, childAncestry)
        val equalLongAgoChild = lp.getLongAgoUpdatedEntity(true, childAncestry)
        val entities = listOf(parentEntity, equalRecentChild, largestChild, equalLongAgoChild)

        every { repo.getHierarchialLeisures() } returns flowOf(entities)

        val resultTree = getLeisureUseCase.getHierarchialLeisures().first()

        assertEquals(1, resultTree.size)
        val resultChildren = resultTree.first().children()
        //assert order
        assertEquals(lp.id_longAgoUpdated, resultChildren[0].value.id)
        assertEquals(lp.id_recentlyUpdated, resultChildren[1].value.id)
        assertEquals(lp.id_largestCounter, resultChildren[2].value.id)
    }

    @Test
    fun `get linearly sorted tasks - tasks should be ordered by counter and updated date`() = runBlocking {
        val lp = LeisureProvider()
        val parentId = 1L
        val childAncestry = AncestryBuilder().withChild(parentId).toString()
        val parentEntitySmallestCounter = LeisureEntity(parentId, "parent", 0, Date(), lp.rootAncestry)
        val largestChild = lp.getLargestCounterEntity(childAncestry)
        val equalRecentChild = lp.getRecentlyUpdatedEntity(true, childAncestry)
        val equalLongAgoChild = lp.getLongAgoUpdatedEntity(true, childAncestry)
        val entities = listOf(parentEntitySmallestCounter, equalRecentChild, largestChild, equalLongAgoChild)
        every { repo.getLinearLeisures() } returns flowOf(entities)

        val resultTree = getLeisureUseCase.getLinearLeisures().first()

        assertEquals(4, resultTree.size)
        //assert order
        assertEquals(parentEntitySmallestCounter.id, resultTree[0].value.id)
        assertEquals(lp.id_longAgoUpdated, resultTree[1].value.id)
        assertEquals(lp.id_recentlyUpdated, resultTree[2].value.id)
        assertEquals(lp.id_largestCounter, resultTree[3].value.id)
    }
}