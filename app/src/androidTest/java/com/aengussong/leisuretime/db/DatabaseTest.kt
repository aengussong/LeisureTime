package com.aengussong.leisuretime.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aengussong.leisuretime.DbRelatedTest
import com.aengussong.leisuretime.data.local.dao.LeisureDao
import com.aengussong.leisuretime.util.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.core.inject

class DatabaseTest : DbRelatedTest() {

    private val leisureDao: LeisureDao by inject()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Test
    fun getLowesCounter_returnsLowestCounterForCurrentLevel() = runBlocking {
        databaseManager.populateDatabase()
        val entityWithLowestCounter = databaseManager.lowestSecondLevel
        val level = entityWithLowestCounter.ancestry

        val result = leisureDao.getLowestCounter(level)

        Assert.assertEquals(entityWithLowestCounter.counter, result)
    }

    @Test
    fun getLeisures_shouldReturnOrderedByAncestry() = runBlocking {
        databaseManager.populateDatabase()
        val expected = databaseManager.getOrderedByAncestry()

        val result = leisureDao.getLeisures().getOrAwaitValue()

        Assert.assertEquals(expected, result)
    }


}