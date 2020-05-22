package com.aengussong.leisuretime

import com.aengussong.leisuretime.util.mockDbModule
import org.junit.After
import org.junit.Before
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin

class DatabaseTest {

    @Before
    fun setUp() {
        loadKoinModules(mockDbModule)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

}