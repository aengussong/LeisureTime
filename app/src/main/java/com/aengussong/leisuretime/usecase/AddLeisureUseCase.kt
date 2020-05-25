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
        val parentAncestry = parentId?.let { repo.getAncestry(parentId) } ?: ROOT_ANCESTRY
        val ancestry = parentId?.let { "$parentAncestry/$parentId" } ?: ROOT_ANCESTRY
        val counter = repo.getLowestCounter(ancestry = parentAncestry)
        return LeisureEntity(name = name, counter = counter, ancestry = ancestry)
    }

}