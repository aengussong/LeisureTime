package com.aengussong.leisuretime.usecase

import com.aengussong.leisuretime.data.LeisureRepository

class DropCountersUseCase(private val repo: LeisureRepository) {

    suspend fun execute() = repo.dropCounters()
}
