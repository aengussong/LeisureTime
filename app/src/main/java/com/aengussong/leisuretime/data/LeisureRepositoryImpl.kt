package com.aengussong.leisuretime.data

import androidx.lifecycle.LiveData
import com.aengussong.leisuretime.data.local.SharedPrefs
import com.aengussong.leisuretime.data.local.dao.LeisureDao
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.model.SortOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class LeisureRepositoryImpl : LeisureRepository, KoinComponent {

    private val localProvider: LeisureDao by inject()
    private val prefs: SharedPrefs by inject()

    override suspend fun addLeisure(leisure: LeisureEntity) = onIO {
        localProvider.addLeisure(leisure)
    }

    override suspend fun getLowestCounter(ancestry: String): Long = onIO {
        localProvider.getLowestCounter(ancestry)
    }

    override suspend fun getAncestry(id: Long): String = onIO {
        localProvider.getAncestry(id)
    }

    override fun getHierarchialLeisures(): LiveData<List<LeisureEntity>> {
        return localProvider.getHierarchialLeisures()
    }

    override fun getLinearLeisures(): Flow<List<LeisureEntity>> {
        return localProvider.getLinearLeisures()
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

    override suspend fun removeLeisures(ancestry: String) = onIO {
        localProvider.removeLeisures(ancestry)
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

    override fun observeLeisure(id: Long) = localProvider.observeLeisure(id)

    override fun toggleSort() {
        val orderToSave = if (prefs.getSortOrder() == SortOrder.LINEAR) {
            SortOrder.HIERARCHY
        } else {
            SortOrder.LINEAR
        }
        prefs.saveSortOrder(orderToSave)
    }

    override fun getSortOrder(): Flow<SortOrder> {
        return prefs.observeSortOrder()
    }

    private suspend fun <T> onIO(block: suspend CoroutineScope.() -> T) =
        withContext(Dispatchers.IO, block = block)
}