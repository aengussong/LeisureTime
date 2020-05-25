package com.aengussong.leisuretime

import com.aengussong.leisuretime.data.local.dao.LeisureDao
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.util.DatabaseManager
import com.aengussong.leisuretime.util.module.mockDbModule
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.core.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.core.inject

class DatabaseTest : KoinComponent {

    private val leisureDao: LeisureDao by inject()

    private val databaseManager = DatabaseManager(leisureDao)

    @Before
    fun setUp() {
        loadKoinModules(mockDbModule)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun getLowesCounter_returnsLowestCounterForCurrentLevel() = runBlocking {
        databaseManager.populateDatabase()
        val entityWithLowestCounter = databaseManager.lowestSecondLevel
        val level = entityWithLowestCounter.ancestry

        val result = leisureDao.getLowestCounter(level)

        Assert.assertEquals(entityWithLowestCounter.counter, result)
    }

}