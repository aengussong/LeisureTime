package com.aengussong.leisuretime.usecase

import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.model.SortOrder
import kotlinx.coroutines.flow.Flow

class SortOrderUseCase(private val repo: LeisureRepository) {

    fun getSortOrder(): Flow<SortOrder> {
        return repo.getSortOrder()
    }

    fun toggleSortOrder() {
        repo.toggleSort()
    }
}