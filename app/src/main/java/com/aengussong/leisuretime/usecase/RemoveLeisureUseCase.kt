package com.aengussong.leisuretime.usecase

import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.util.AncestryBuilder

class RemoveLeisureUseCase(private val repo: LeisureRepository) {

    suspend fun execute(id: Long) {
        val isRoot = AncestryBuilder(repo.getLeisure(id).ancestry).isRoot()
        if (isRoot) {
            repo.removeRootLeisure(id)
        } else {
            repo.removeLeisure(id)
        }
    }
}