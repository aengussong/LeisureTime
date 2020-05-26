package com.aengussong.leisuretime

import com.aengussong.leisuretime.util.rule.InMemoryDatabaseRule
import org.junit.Rule
import org.koin.test.KoinTest

open class DbRelatedTest: KoinTest {

    @get:Rule
    val inMemoryDbRule = InMemoryDatabaseRule()
}