package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.LeisureRepository
import com.aengussong.prioritytime.util.AncestryBuilder

class IncrementLeisureUseCase(private val repo: LeisureRepository) {

    suspend fun execute(id: Long) {
        val entity = repo.getLeisure(id)
        val idsToIncrement =
            AncestryBuilder(entity.ancestry).withChild(entity.id).getAncestryIds()

        repo.incrementLeisures(idsToIncrement)
    }

    suspend fun incrementMinLeisure() {
        repo.getMinLeisure()?.id?.let {
            execute(it)
        }
    }
}
