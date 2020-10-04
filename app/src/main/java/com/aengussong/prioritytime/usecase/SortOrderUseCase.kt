package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.LeisureRepository
import com.aengussong.prioritytime.model.SortOrder
import kotlinx.coroutines.flow.Flow

class SortOrderUseCase(private val repo: LeisureRepository) {

    fun getSortOrder(): Flow<SortOrder> {
        return repo.getSortOrder()
    }

    fun toggleSortOrder() {
        repo.toggleSort()
    }
}