package com.aengussong.leisuretime.util

import com.aengussong.leisuretime.data.local.dao.LeisureDao
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import org.koin.core.KoinComponent

class DatabaseManager(private val leisureDao: LeisureDao) : KoinComponent {

    private val rootAncestry = AncestryBuilder()
    private val firstLevelAncestry = rootAncestry.toString()
    private val firstLevelId = 1L
    private val secondLevelAncestry = rootAncestry.addChild(firstLevelId).toString()
    private val secondLevelId = 10L
    private val thirdLevelAncestry = rootAncestry.addChild(secondLevelId).toString()
    private val thirdLevelId = 100L

    private val lowestCounter = 2L

    /*---------------FIRST LEVEL------------------*/
    val lowestFirstLevel =
        LeisureEntity(
            id = firstLevelId,
            name = "firstLvl",
            counter = lowestCounter,
            ancestry = firstLevelAncestry
        )

    /*---------------SECOND LEVEL------------------*/
    val lowestSecondLevel =
        LeisureEntity(
            id = secondLevelId,
            name = "secondLvl",
            counter = lowestCounter,
            ancestry = secondLevelAncestry
        )

    /*---------------THIRD LEVEL------------------*/
    val lowestThirdLevel =
        LeisureEntity(
            id = thirdLevelId,
            name = "thirdLvl",
            counter = lowestCounter,
            ancestry = thirdLevelAncestry
        )

    private val data = listOf(
        lowestFirstLevel,
        lowestSecondLevel,
        lowestThirdLevel
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