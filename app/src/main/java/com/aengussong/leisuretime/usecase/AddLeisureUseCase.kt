package com.aengussong.leisuretime.usecase

import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.util.AncestryBuilder

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
        val ancestry = AncestryBuilder(parentAncestry).addChild(parentId).toString()
        val counter = repo.getLowestCounter(parentAncestry)
        return LeisureEntity(name, counter, ancestry)
    }

    private suspend fun createRootLeisure(name: String): LeisureEntity {
        val ancestry = AncestryBuilder().toString()
        val counter = repo.getLowestCounter(ancestry)
        return LeisureEntity(name, counter, ancestry)
    }

}