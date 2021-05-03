package com.aengussong.prioritytime.data.local

import android.content.Context
import com.aengussong.prioritytime.model.SortOrder
import com.aengussong.prioritytime.worker.Work
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

private const val TASK_PREFS = "leisure_prefs"

private const val KEY_SORT = "sort_id"
private const val KEY_ERASE = "erase_option"

private val DEFAULT_SORT = SortOrder.HIERARCHY.id

@ExperimentalCoroutinesApi
class SharedPrefs(context: Context) {

    private val prefs = context.getSharedPreferences(TASK_PREFS, Context.MODE_PRIVATE)

    private val sortOrderChannel = ConflatedBroadcastChannel(getSortOrder())
    private val eraseOptionChannel = ConflatedBroadcastChannel(getEraseOption())

    fun getSortOrder(): SortOrder {
        return when (val id = prefs.getInt(KEY_SORT, DEFAULT_SORT)) {
            SortOrder.HIERARCHY.id -> SortOrder.HIERARCHY
            SortOrder.LINEAR.id -> SortOrder.LINEAR
            else -> throw IllegalArgumentException("Unknown sort order id: $id")
        }
    }

    fun saveSortOrder(order: SortOrder) {
        val saved = prefs.edit().putInt(KEY_SORT, order.id).commit()
        if (saved) {
            sortOrderChannel.offer(order)
        }
    }

    fun saveEraseOption(work: Work) {
        prefs.edit().putString(KEY_ERASE, work.toString()).apply()
        eraseOptionChannel.offer(work)
    }

    private fun getEraseOption(): Work {
        val savedWork = prefs.getString(KEY_ERASE, Work.ERASE_NEVER.toString())!!
        return Work.valueOf(savedWork)
    }

    fun getEraseOptionFlow(): Flow<Work> = eraseOptionChannel.asFlow()

    @FlowPreview
    fun observeSortOrder() = sortOrderChannel.asFlow()
}