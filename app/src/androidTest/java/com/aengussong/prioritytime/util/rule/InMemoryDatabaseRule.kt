package com.aengussong.prioritytime.util.rule

import com.aengussong.prioritytime.util.module.inMemoryDb
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.core.context.loadKoinModules

class InMemoryDatabaseRule : TestWatcher() {

    override fun starting(description: Description?) {
        loadKoinModules(inMemoryDb)
    }
}