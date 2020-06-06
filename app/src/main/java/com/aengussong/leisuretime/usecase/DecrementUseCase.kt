package com.aengussong.leisuretime.usecase

import com.aengussong.leisuretime.data.LeisureRepository

class DecrementUseCase(private val repo: LeisureRepository) {

    suspend fun execute(leisureId: Long) {
        repo.getLeisureCounter(leisureId).also {
            if (it > 0L) {
                repo.setCounter(leisureId, it - 1)
            }
        }
    }
}
