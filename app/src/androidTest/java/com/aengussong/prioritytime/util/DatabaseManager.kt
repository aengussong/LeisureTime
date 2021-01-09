package com.aengussong.prioritytime.util

import com.aengussong.prioritytime.data.local.dao.LeisureDao
import com.aengussong.prioritytime.data.local.entity.LeisureEntity
import org.koin.core.KoinComponent

class DatabaseManager(private val leisureDao: LeisureDao) : KoinComponent {

    private val rootAncestry = AncestryBuilder()
    private val firstLevelAncestry = rootAncestry.toString()
    private val firstLevelId = 1L
    private val secondLevelAncestry = rootAncestry.withChild(firstLevelId).toString()
    private val secondLevelId = 10L
    private val thirdLevelAncestry = rootAncestry.withChild(secondLevelId).toString()
    private val thirdLevelId = 100L

    private val lowestCounter = 2L

    /*---------------GENERIC----------------------*/
    val genericEntity = LeisureEntity(
        id = 0L,
        name = "generic",
        counter = 0L,
        ancestry = AncestryBuilder().toString()
    )

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

    suspend fun populateDatabase(vararg leisure: LeisureEntity) {
        leisure.forEach { leisureDao.addLeisure(it) }
    }

    fun getOrderedByAncestry(): List<LeisureEntity> {
        return data.sortedWith(compareBy { it.ancestry })
    }

    suspend fun populateDatabaseWithChildren(
        rootLeisure: LeisureEntity,
        childrenCount: Int
    ): List<LeisureEntity> {
        val ancestry = AncestryBuilder(rootLeisure.ancestry).withChild(rootLeisure.id).toString()
        val children = mutableListOf<LeisureEntity>()
        for (i in 0 until childrenCount) {
            val leisure = genericEntity.copy(ancestry = ancestry)
            val id = leisureDao.addLeisure(leisure)
            children.add(leisure.copy(id = id))
        }
        return children
    }
}