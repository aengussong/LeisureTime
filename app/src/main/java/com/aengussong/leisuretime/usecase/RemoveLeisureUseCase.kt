package com.aengussong.leisuretime.usecase

import com.aengussong.leisuretime.data.LeisureRepository

class RemoveLeisureUseCase(private val repo: LeisureRepository) {

    suspend fun execute(id: Long) = repo.removeLeisure(id)
}