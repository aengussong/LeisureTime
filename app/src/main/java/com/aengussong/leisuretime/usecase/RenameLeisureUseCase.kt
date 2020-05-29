package com.aengussong.leisuretime.usecase

import com.aengussong.leisuretime.data.LeisureRepository

class RenameLeisureUseCase(private val repo: LeisureRepository) {

    suspend fun execute(leisureId: Long, newName: String) =
        repo.renameLeisure(leisureId, newName)
}
