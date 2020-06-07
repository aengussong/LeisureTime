package com.aengussong.leisuretime.data

import androidx.lifecycle.LiveData
import com.aengussong.leisuretime.data.local.entity.LeisureEntity

interface LeisureRepository {
    suspend fun addLeisure(leisure: LeisureEntity): Long
    suspend fun getLowestCounter(ancestry: String): Long
    suspend fun getAncestry(id: Long): String
    fun getLeisures(): LiveData<List<LeisureEntity>>
    suspend fun getLeisure(id: Long): LeisureEntity
    suspend fun incrementLeisures(ids: List<Long>)
    suspend fun renameLeisure(id: Long, newName: String)
    suspend fun removeLeisure(id: Long)
    suspend fun removeLeisures(ancestry: String)
    suspend fun dropCounters()
    suspend fun getLeisureCounter(id: Long): Long
    suspend fun setCounter(id: Long, counter: Long)
    fun observeLeisure(id: Long): LiveData<LeisureEntity>
}