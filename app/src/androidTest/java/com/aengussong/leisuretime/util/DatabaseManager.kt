package com.aengussong.leisuretime.util

import com.aengussong.leisuretime.data.local.dao.LeisureDao
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import org.koin.core.KoinComponent

private const val FIRST_LVL_ANCESTRY = ROOT_ANCESTRY
private const val FIRST_LVL_ID = 1L
private const val SECOND_LVL_ANCESTRY = "$FIRST_LVL_ANCESTRY$FIRST_LVL_ID/"
private const val SECOND_LVL_ID = 2L
private const val THIRD_LVL_ANCESTRY = "$SECOND_LVL_ANCESTRY$SECOND_LVL_ID/"
private const val THIRD_LVL_ID = 3L

private const val LOWEST_COUNTER = 2L

class DatabaseManager(private val leisureDao: LeisureDao) : KoinComponent {

    /*---------------FIRST LEVEL------------------*/
    val lowestFirstLevel =
        LeisureEntity(
            id = FIRST_LVL_ID,
            name = "firstLvl",
            counter = LOWEST_COUNTER,
            ancestry = FIRST_LVL_ANCESTRY
        )

    /*---------------SECOND LEVEL------------------*/
    val lowestSecondLevel =
        LeisureEntity(
            id = SECOND_LVL_ID,
            name = "secondLvl",
            counter = LOWEST_COUNTER,
            ancestry = SECOND_LVL_ANCESTRY
        )

    /*---------------THIRD LEVEL------------------*/
    val lowestThirdLevel =
        LeisureEntity(
            id = THIRD_LVL_ID,
            name = "thirdLvl",
            counter = LOWEST_COUNTER,
            ancestry = THIRD_LVL_ANCESTRY
        )

    private val data = listOf(
        lowestThirdLevel,
        lowestSecondLevel,
        lowestFirstLevel
    )

    suspend fun populateDatabase(): List<LeisureEntity> {
        return data.apply {
            this.forEach {
                leisureDao.addLeisure(it)
            }
        }
    }

    fun getOrderedByAncestry(): List<LeisureEntity> {
        return data.sortedWith(compareBy { it.ancestry })
    }
}