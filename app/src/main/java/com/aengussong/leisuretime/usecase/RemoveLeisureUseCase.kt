package com.aengussong.leisuretime.usecase

import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.util.AncestryBuilder

class RemoveLeisureUseCase(private val repo: LeisureRepository) {

    suspend fun execute(id: Long) {
        val ancestry = repo.getAncestry(id)
        val isRoot = AncestryBuilder(ancestry).isRoot()
        if (!isRoot) {
            val childrenAncestry = AncestryBuilder(ancestry).withChild(id).toString()
            //remove children of leisure
            repo.removeLeisures(childrenAncestry)
        }
        //remove leisure
        repo.removeLeisure(id)
    }
}