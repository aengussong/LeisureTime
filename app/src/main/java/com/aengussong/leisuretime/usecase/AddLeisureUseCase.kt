package com.aengussong.leisuretime.usecase

import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.util.ROOT_ANCESTRY

class AddLeisureUseCase(private val repo: LeisureRepository) {

    suspend fun execute(name: String, parentId: Long? = null) {
        val leisure = createLeisure(name, parentId)
        repo.addLeisure(leisure)
    }

    private suspend fun createLeisure(name: String, parentId: Long?): LeisureEntity {
        return parentId?.let {
            createSubLeisure(name, it)
        } ?: createRootLeisure(name)
    }

    private suspend fun createSubLeisure(name: String, parentId: Long): LeisureEntity {
        val parentAncestry = repo.getAncestry(parentId)
        if (parentAncestry.contains(parentId.toString())) throw CyclingReferenceException()
        val ancestry = "$parentAncestry/$parentId"
        val counter = repo.getLowestCounter(parentAncestry)
        return LeisureEntity(name, counter, ancestry)
    }

    private suspend fun createRootLeisure(name: String): LeisureEntity {
        val ancestry = ROOT_ANCESTRY
        val counter = repo.getLowestCounter(ancestry)
        return LeisureEntity(name, counter, ancestry)
    }

}

class CyclingReferenceException(msg: String = "Entity can't contain cycling reference in ancestry tree") :
    Exception(msg)