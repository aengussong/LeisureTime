package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.LeisureRepository
import com.aengussong.prioritytime.util.AncestryBuilder

class RemoveLeisureUseCase(private val repo: LeisureRepository) {

    suspend fun execute(id: Long) {
        val ancestry = repo.getAncestry(id)

        //remove children of leisure
        val childrenAncestry = AncestryBuilder(ancestry).withChild(id).toString()
        repo.removeLeisures(childrenAncestry)

        //remove leisure
        repo.removeLeisure(id)
    }
}