package com.aengussong.prioritytime.util

import com.aengussong.prioritytime.data.local.dao.TasksDao
import com.aengussong.prioritytime.data.local.entity.TaskEntity
import org.koin.core.KoinComponent

class DatabaseManager(private val tasksDao: TasksDao) : KoinComponent {

    private val rootAncestry = AncestryBuilder()
    private val firstLevelAncestry = rootAncestry.toString()
    private val firstLevelId = 1L
    private val secondLevelAncestry = rootAncestry.withChild(firstLevelId).toString()
    private val secondLevelId = 10L
    private val thirdLevelAncestry = rootAncestry.withChild(secondLevelId).toString()
    private val thirdLevelId = 100L

    private val lowestCounter = 2L

    /*---------------GENERIC----------------------*/
    val genericEntity = TaskEntity(
        id = 0L,
        name = "generic",
        counter = 0L,
        ancestry = AncestryBuilder().toString()
    )

    /*---------------FIRST LEVEL------------------*/
    val lowestFirstLevel =
        TaskEntity(
            id = firstLevelId,
            name = "firstLvl",
            counter = lowestCounter,
            ancestry = firstLevelAncestry
        )

    /*---------------SECOND LEVEL------------------*/
    val lowestSecondLevel =
        TaskEntity(
            id = secondLevelId,
            name = "secondLvl",
            counter = lowestCounter,
            ancestry = secondLevelAncestry
        )

    /*---------------THIRD LEVEL------------------*/
    val lowestThirdLevel =
        TaskEntity(
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

    suspend fun populateDatabase(): List<TaskEntity> {
        return data.apply {
            this.forEach {
                tasksDao.addTask(it)
            }
        }
    }

    suspend fun populateDatabase(vararg task: TaskEntity) {
        task.forEach { tasksDao.addTask(it) }
    }

    fun getOrderedByAncestry(): List<TaskEntity> {
        return data.sortedWith(compareBy { it.ancestry })
    }

    suspend fun populateDatabaseWithChildren(
        rootTask: TaskEntity,
        childrenCount: Int
    ): List<TaskEntity> {
        val ancestry = AncestryBuilder(rootTask.ancestry).withChild(rootTask.id).toString()
        val children = mutableListOf<TaskEntity>()
        for (i in 0 until childrenCount) {
            val task = genericEntity.copy(ancestry = ancestry)
            val id = tasksDao.addTask(task)
            children.add(task.copy(id = id))
        }
        return children
    }
}