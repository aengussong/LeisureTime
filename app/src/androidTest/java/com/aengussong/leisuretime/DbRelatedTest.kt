package com.aengussong.leisuretime

import com.aengussong.leisuretime.data.local.LeisureDb
import com.aengussong.leisuretime.data.local.dao.LeisureDao
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.util.DatabaseManager
import com.aengussong.leisuretime.util.rule.InMemoryDatabaseRule
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.ClassRule
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.*

private val rootElement = LeisureEntity(1L, "root", 0, Date(), "")

open class DbRelatedTest : KoinTest {

    companion object {
        @get:ClassRule
        val inMemoryRule = InMemoryDatabaseRule()
    }

    private val db: LeisureDb by inject()
    private val leisureDao: LeisureDao by inject()
    protected val databaseManager = DatabaseManager(leisureDao)

    @Before
    fun insertRootElement() = runBlocking {
        leisureDao.addLeisure(rootElement)
    }

    @After
    fun cleanDb() {
        db.clearAllTables()
    }
}