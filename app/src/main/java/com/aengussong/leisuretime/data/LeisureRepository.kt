package com.aengussong.leisuretime.data

import androidx.lifecycle.LiveData
import com.aengussong.leisuretime.data.local.entity.LeisureEntity

interface LeisureRepository {
    suspend fun addLeisure(leisure: LeisureEntity)
    suspend fun getLowestCounter(ancestry: String): Long
    suspend fun getAncestry(id: Long): String
    fun getLeisures(): LiveData<List<LeisureEntity>>
    suspend fun getLeisure(id: Long): LeisureEntity
    suspend fun incrementLeisures(ids: List<Long>)
    suspend fun renameLeisure(id: Long, newName: String)
    suspend fun removeLeisure(id: Long)
}