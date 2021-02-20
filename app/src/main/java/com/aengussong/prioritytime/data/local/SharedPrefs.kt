package com.aengussong.prioritytime.data.local

import android.content.Context
import com.aengussong.prioritytime.model.SortOrder
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow

private const val TASK_PREFS = "leisure_prefs"

private const val KEY_SORT = "sort_id"

private val DEFAULT_SORT = SortOrder.HIERARCHY.id

class SharedPrefs(context: Context) {

    private val prefs = context.getSharedPreferences(TASK_PREFS, Context.MODE_PRIVATE)

    private val channel = ConflatedBroadcastChannel(getSortOrder())

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
            channel.offer(order)
        }
    }

    fun observeSortOrder() = channel.asFlow()
}