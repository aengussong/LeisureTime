package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.LeisureRepository

class DropCountersUseCase(private val repo: LeisureRepository) {

    suspend fun execute() = repo.dropCounters()
}
