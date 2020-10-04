package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.LeisureRepository
import com.aengussong.prioritytime.data.local.entity.LeisureEntity
import com.aengussong.prioritytime.util.AncestryBuilder

class AddLeisureUseCase(private val repo: LeisureRepository) {

    suspend fun execute(name: String, parentId: Long? = null) {
        val leisure = parentId?.let {
            createSubLeisure(name, it)
        } ?: createRootLeisure(name)
        repo.addLeisure(leisure)
    }

    private suspend fun createSubLeisure(name: String, parentId: Long): LeisureEntity {
        val parentAncestry = repo.getAncestry(parentId)
        val ancestry = AncestryBuilder(parentAncestry).withChild(parentId).toString()
        val counter = repo.getLowestCounter(ancestry).let {
            if (it == -1L) {
                return@let repo.getLeisureCounter(parentId)
            }
            it
        }
        return LeisureEntity(name, counter, ancestry)
    }

    private suspend fun createRootLeisure(name: String): LeisureEntity {
        //root ancestry
        val ancestry = AncestryBuilder().toString()
        val counter = repo.getLowestCounter(ancestry)
        return LeisureEntity(name, counter, ancestry)
    }

}