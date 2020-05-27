package com.aengussong.leisuretime

import com.aengussong.leisuretime.data.local.LeisureDb
import com.aengussong.leisuretime.data.local.dao.LeisureDao
import com.aengussong.leisuretime.util.DatabaseManager
import com.aengussong.leisuretime.util.rule.InMemoryDatabaseRule
import org.junit.After
import org.junit.ClassRule
import org.koin.test.KoinTest
import org.koin.test.inject

open class DbRelatedTest : KoinTest {

    companion object {
        @get:ClassRule
        val inMemoryRule = InMemoryDatabaseRule()
    }

    private val db: LeisureDb by inject()
    private val leisureDao: LeisureDao by inject()
    protected val databaseManager = DatabaseManager(leisureDao)

    @After
    fun cleanDb() {
        db.clearAllTables()
    }
}