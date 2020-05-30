package com.aengussong.leisuretime.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.testUtils.LeisureProvider
import com.aengussong.leisuretime.testUtils.getOrAwaitValue
import com.aengussong.leisuretime.util.AncestryBuilder
import io.mockk.every
import io.mockk.mockk
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
    fun `get data with three levels - should be converted to tree hierarchy`() {
        val ancestryBuilder = AncestryBuilder()

        val firstLevelAncestry = ancestryBuilder.toString()
        val firstLevelId = 2L
        val firstLevelName = "firstLevel"

        val secondLevelAncestry = ancestryBuilder.addChild(firstLevelId).toString()
        val secondLevelId = 3L
        val secondLevelName = "secondLevel"

        val thirdLevelAncestry = ancestryBuilder.addChild(secondLevelId).toString()
        val thirdLevelId = 4L
        val thirdLevelName = "thirdLevel"

        val first = LeisureEntity(firstLevelId, firstLevelName, 2, Date(), firstLevelAncestry)
        val second = LeisureEntity(secondLevelId, secondLevelName, 2, Date(), secondLevelAncestry)
        val third = LeisureEntity(thirdLevelId, thirdLevelName, 2, Date(), thirdLevelAncestry)

        every { repo.getLeisures() } returns MutableLiveData(listOf(first, second, third))

        val resultTree = getLeisureUseCase.execute().getOrAwaitValue()

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
    fun `get data with several root leisures - should return several unconnected trees`() {
        val rootAncestry = AncestryBuilder()

        val firstLevelAncestry = rootAncestry.toString()

        val parentId = 2L
        val secondLevelAncestry = rootAncestry.addChild(parentId).toString()

        val first = LeisureEntity(1L, "first", 2, Date(), firstLevelAncestry)
        val second = LeisureEntity(parentId, "second", 2, Date(), firstLevelAncestry)
        val third = LeisureEntity(3L, "third", 2, Date(), secondLevelAncestry)

        every { repo.getLeisures() } returns MutableLiveData(listOf(first, second, third))

        val resultTree = getLeisureUseCase.execute().getOrAwaitValue()

        assertEquals(2, resultTree.size)
        assertEquals(1, resultTree.first().levels())
        assertEquals(2, resultTree[1].levels())
    }

    @Test
    fun `get data - root trees should be ordered by counter and updated date`() {
        val lp = LeisureProvider()
        val recentlyUpdated = lp.getRecentlyUpdatedEntity(true)
        val largestCounter = lp.getLargestCounterEntity()
        val longAgoUpdated = lp.getLongAgoUpdatedEntity(true)
        val entities = listOf(recentlyUpdated, largestCounter, longAgoUpdated)
        every { repo.getLeisures() } returns MutableLiveData(entities)

        val resultTree = getLeisureUseCase.execute().getOrAwaitValue()

        //assert order
        assertEquals(lp.id_longAgoUpdated, resultTree[0].value.id)
        assertEquals(lp.id_recentlyUpdated, resultTree[1].value.id)
        assertEquals(lp.id_largestCounter, resultTree[2].value.id)
    }

    @Test
    fun `get trees - subtrees should be ordered by counter and updated date`() {
        val lp = LeisureProvider()

        val parentId = 1L
        val childAncestry = AncestryBuilder().addChild(parentId).toString()

        val parentEntity = LeisureEntity(parentId, "parent", 0, Date(), lp.rootAncestry)
        val largestChild = lp.getLargestCounterEntity(childAncestry)
        val equalRecentChild = lp.getRecentlyUpdatedEntity(true, childAncestry)
        val equalLongAgoChild = lp.getLongAgoUpdatedEntity(true, childAncestry)
        val entities = listOf(parentEntity, equalRecentChild, largestChild, equalLongAgoChild)

        every { repo.getLeisures() } returns MutableLiveData(entities)

        val resultTree = getLeisureUseCase.execute().getOrAwaitValue()

        assertEquals(1, resultTree.size)
        val resultChildren = resultTree.first().children()
        //assert order
        assertEquals(lp.id_longAgoUpdated, resultChildren[0].value.id)
        assertEquals(lp.id_recentlyUpdated, resultChildren[1].value.id)
        assertEquals(lp.id_largestCounter, resultChildren[2].value.id)
    }
}