package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.TaskRepository
import com.aengussong.prioritytime.model.SortOrder
import kotlinx.coroutines.flow.Flow

class SortOrderUseCase(private val repo: TaskRepository) {

    fun getSortOrder(): Flow<SortOrder> {
        return repo.getSortOrder()
    }

    fun toggleSortOrder() {
        repo.toggleSort()
    }
}