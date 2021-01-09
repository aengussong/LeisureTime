package com.aengussong.prioritytime.data

import com.aengussong.prioritytime.data.local.SharedPrefs
import com.aengussong.prioritytime.data.local.dao.LeisureDao
import com.aengussong.prioritytime.data.local.entity.LeisureEntity
import com.aengussong.prioritytime.model.SortOrder
import com.aengussong.prioritytime.util.ROOT_ANCESTRY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent

class LeisureRepositoryImpl(
    private val localProvider: LeisureDao,
    private val prefs: SharedPrefs
) : LeisureRepository, KoinComponent {

    override suspend fun addLeisure(leisure: LeisureEntity) = onIO {
        localProvider.addLeisure(leisure)
    }

    /**
     * @return lowest counter for the current ancestry or 0 if there are no items with such ancestry.
     * */
    override suspend fun getLowestCounter(ancestry: String): Long = onIO {
        localProvider.getLowestCounter(ancestry)
    }

    override suspend fun getAncestry(id: Long): String = onIO {
        localProvider.getAncestry(id)
    }

    override fun getHierarchialLeisures(): Flow<List<LeisureEntity>> {
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

    override fun observeLeisure(id: Long) = localProvider.observeLeisureDistinct(id)

    override fun observeMinLeisure(): Flow<LeisureEntity?> {
        return getSortOrder().flatMapLatest { order ->
            when (order) {
                SortOrder.HIERARCHY -> localProvider.observeMinHierarchial(ROOT_ANCESTRY)
                SortOrder.LINEAR -> localProvider.observeMinLinear()
            }.distinctUntilChanged()
        }
    }

    override suspend fun getMinLeisure(): LeisureEntity? {
        return when (prefs.getSortOrder()) {
            SortOrder.HIERARCHY -> localProvider.getMinHierarchial(ROOT_ANCESTRY)
            SortOrder.LINEAR -> localProvider.getMinLinear()
        }
    }

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