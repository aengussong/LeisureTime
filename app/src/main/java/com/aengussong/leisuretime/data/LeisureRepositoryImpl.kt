package com.aengussong.leisuretime.data

import androidx.lifecycle.LiveData
import com.aengussong.leisuretime.data.local.dao.LeisureDao
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class LeisureRepositoryImpl : LeisureRepository, KoinComponent {

    private val localProvider: LeisureDao by inject()

    override suspend fun addLeisure(leisure: LeisureEntity) = onIO {
        localProvider.addLeisure(leisure)
    }

    override suspend fun getLowestCounter(ancestry: String): Long = onIO {
        localProvider.getLowestCounter(ancestry)
    }

    override suspend fun getAncestry(id: Long): String = onIO {
        localProvider.getAncestry(id)
    }

    override fun getLeisures(): LiveData<List<LeisureEntity>> {
        return localProvider.getLeisures()
    }

    override suspend fun getLeisure(id: Long): LeisureEntity = onIO {
        localProvider.getLeisure(id)
    }

    override suspend fun incrementLeisures(ids: List<Long>) = onIO {
        localProvider.incrementLeisures(ids)
    }

    override suspend fun renameLeisure(id: Long, newName: String) = onIO {
        localProvider.renameLeisure(id, newName)
    }

    override suspend fun removeLeisure(id: Long) = onIO {
        localProvider.removeLeisure(id)
    }

    override suspend fun dropCounters() = onIO {
        localProvider.dropCounters()
    }

    override suspend fun getLeisureCounter(id: Long) = onIO {
        localProvider.getCounter(id)
    }

    override suspend fun setCounter(id: Long, counter: Long) = onIO {
        localProvider.updateCounter(id, counter)
    }

    private suspend fun <T> onIO(block: suspend CoroutineScope.() -> T) =
        withContext(Dispatchers.IO, block = block)
}