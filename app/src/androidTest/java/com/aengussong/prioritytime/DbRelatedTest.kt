package com.aengussong.prioritytime

import com.aengussong.prioritytime.data.local.LeisureDb
import com.aengussong.prioritytime.data.local.dao.LeisureDao
import com.aengussong.prioritytime.util.DatabaseManager
import com.aengussong.prioritytime.util.rule.InMemoryDatabaseRule
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