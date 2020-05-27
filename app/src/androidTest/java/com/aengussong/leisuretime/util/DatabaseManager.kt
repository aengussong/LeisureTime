package com.aengussong.leisuretime.util

import com.aengussong.leisuretime.data.local.dao.LeisureDao
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import org.koin.core.KoinComponent

private const val FIRST_LVL = "1"
private const val SECOND_LVL = "1/2"
private const val THIRD_LVL = "1/2/3"

private const val LOWEST_COUNTER = 2L

class DatabaseManager(private val leisureDao: LeisureDao) : KoinComponent {

    /*---------------FIRST LEVEL------------------*/
    val lowestFirstLevel =
        LeisureEntity(name = "fake", counter = LOWEST_COUNTER, ancestry = FIRST_LVL)

    /*---------------SECOND LEVEL------------------*/
    val lowestSecondLevel =
        LeisureEntity(name = "fake", counter = LOWEST_COUNTER, ancestry = SECOND_LVL)

    /*---------------THIRD LEVEL------------------*/
    val lowestThirdLevel =
        LeisureEntity(name = "fake", counter = LOWEST_COUNTER, ancestry = THIRD_LVL)

    private val data = arrayOf(
        lowestFirstLevel,
        lowestSecondLevel,
        lowestThirdLevel
    )

    suspend fun populateDatabase(): Array<LeisureEntity> {
        return data.apply {
            this.forEach {
                leisureDao.addLeisure(it)
            }
        }
    }
}