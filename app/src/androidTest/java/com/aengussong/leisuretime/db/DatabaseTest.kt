package com.aengussong.leisuretime.db

import com.aengussong.leisuretime.DbRelatedTest
import com.aengussong.leisuretime.data.local.dao.LeisureDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.koin.core.inject

class DatabaseTest : DbRelatedTest() {

    private val leisureDao: LeisureDao by inject()

    @Test
    fun getLowesCounter_returnsLowestCounterForCurrentLevel() = runBlocking {
        databaseManager.populateDatabase()
        val entityWithLowestCounter = databaseManager.lowestSecondLevel
        val level = entityWithLowestCounter.ancestry

        val result = leisureDao.getLowestCounter(level)

        Assert.assertEquals(entityWithLowestCounter.counter, result)
    }

}