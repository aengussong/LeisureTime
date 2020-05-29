package com.aengussong.leisuretime.usecase

import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.util.AncestryBuilder

class IncrementLeisureUseCase(private val repo: LeisureRepository) {

    suspend fun execute(id: Long) {
        val entity = repo.getLeisure(id)
        val idsToIncrement =
            AncestryBuilder(entity.ancestry).addChild(entity.id).getAncestryIds()

        repo.incrementLeisures(idsToIncrement)
    }
}
