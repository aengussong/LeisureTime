package com.aengussong.prioritytime.testUtils

import com.aengussong.prioritytime.data.local.entity.LeisureEntity
import com.aengussong.prioritytime.util.AncestryBuilder
import java.util.*

class LeisureProvider() {
    val rootAncestry = AncestryBuilder().toString()
    val id_largestCounter = 2L
    val id_recentlyUpdated = 3L
    val id_longAgoUpdated = 4L
    val counter_largest = 10L
    val counter_recentlyUpdated = 2L
    val counter_longAgoUpdated = 4L
    val counter_equal = 5L
    val recently = Date()
    val longAgo = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, -1) }.time

    fun getLargestCounterEntity(ancestry: String = rootAncestry) =
        LeisureEntity(id_largestCounter, "largest", counter_largest, recently, ancestry)

    fun getRecentlyUpdatedEntity(
        isEqualCounter: Boolean = false,
        ancestry: String = rootAncestry
    ): LeisureEntity {
        val counter = if (isEqualCounter) counter_equal else counter_recentlyUpdated
        return LeisureEntity(
            id_recentlyUpdated,
            "recently",
            counter,
            recently,
            ancestry
        )
    }

    fun getLongAgoUpdatedEntity(
        isEqualCounter: Boolean = false,
        ancestry: String = rootAncestry
    ): LeisureEntity {
        val counter = if (isEqualCounter) counter_equal else counter_longAgoUpdated
        return LeisureEntity(
            id_longAgoUpdated,
            "longAgo",
            counter,
            longAgo,
            ancestry
        )
    }

    companion object {
        /**
         * @returns the most generic leisure entity, which should not be used in batch operations
         * with another entities due to possible id conflict
         * */
        fun getGenericEntity() =
            LeisureEntity(1L, "generic", 0, Date(), AncestryBuilder().toString())
    }
}