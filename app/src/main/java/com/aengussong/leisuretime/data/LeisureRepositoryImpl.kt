package com.aengussong.leisuretime.data

import androidx.lifecycle.LiveData
import com.aengussong.leisuretime.data.local.dao.LeisureDao
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class LeisureRepositoryImpl : LeisureRepository, KoinComponent {

    private val localProvider: LeisureDao by inject()

    override suspend fun addLeisure(leisure: LeisureEntity) = withContext(Dispatchers.IO) {
        localProvider.addLeisure(leisure)
    }

    override suspend fun getLowestCounter(ancestry: String): Long = withContext(Dispatchers.IO) {
        localProvider.getLowestCounter(ancestry)
    }

    override suspend fun getAncestry(id: Long): String = withContext(Dispatchers.IO) {
        localProvider.getAncestry(id)
    }

    override fun getLeisures(): LiveData<List<LeisureEntity>> {
        return localProvider.getLeisures()
    }

    override suspend fun getLeisure(id: Long): LeisureEntity = withContext(Dispatchers.IO) {
        localProvider.getLeisure(id)
    }

    override suspend fun incrementLeisures(ids: List<Long>) = withContext(Dispatchers.IO) {
        localProvider.incrementLeisures(ids)
    }
}