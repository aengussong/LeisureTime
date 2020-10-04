package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.LeisureRepository

class RenameLeisureUseCase(private val repo: LeisureRepository) {

    suspend fun execute(leisureId: Long, newName: String) =
        repo.renameLeisure(leisureId, newName)
}
