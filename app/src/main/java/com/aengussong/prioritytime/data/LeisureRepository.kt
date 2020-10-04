package com.aengussong.prioritytime.data

import androidx.lifecycle.LiveData
import com.aengussong.prioritytime.data.local.entity.LeisureEntity
import com.aengussong.prioritytime.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface LeisureRepository {
    suspend fun addLeisure(leisure: LeisureEntity): Long
    suspend fun getLowestCounter(ancestry: String): Long
    suspend fun getAncestry(id: Long): String
    fun getHierarchialLeisures(): LiveData<List<LeisureEntity>>
    fun getLinearLeisures(): Flow<List<LeisureEntity>>
    suspend fun getLeisure(id: Long): LeisureEntity
    suspend fun incrementLeisures(ids: List<Long>)
    suspend fun renameLeisure(id: Long, newName: String)
    suspend fun removeLeisure(id: Long)
    suspend fun removeLeisures(ancestry: String)
    suspend fun dropCounters()
    suspend fun getLeisureCounter(id: Long): Long
    suspend fun setCounter(id: Long, counter: Long)
    fun observeLeisure(id: Long): LiveData<LeisureEntity>
    fun toggleSort()
    fun getSortOrder(): Flow<SortOrder>
}