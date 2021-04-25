package com.aengussong.prioritytime

import com.aengussong.prioritytime.data.local.TasksDb
import com.aengussong.prioritytime.data.local.dao.TasksDao
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

    private val db: TasksDb by inject()
    private val tasksDao: TasksDao by inject()
    protected val databaseManager = DatabaseManager(tasksDao)

    @After
    fun cleanDb() {
        db.clearAllTables()
    }
}