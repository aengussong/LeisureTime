package com.aengussong.leisuretime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aengussong.leisuretime.util.ROOT_ANCESTRY
import java.util.*

@Entity
data class LeisureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val counter: Long = 0,
    val updated: Date = Date(),
    /**
     * ancestry written in ids  sequence, with slash as delimiter, e.g. "2/5"
     * that means that current entity is child for entity with id 5, which, in his turn, child of
     * entity with id 2
     */
    val ancestry: String
)