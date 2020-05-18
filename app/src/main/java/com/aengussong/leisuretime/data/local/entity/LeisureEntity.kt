package com.aengussong.leisuretime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class LeisureEntity(
    @PrimaryKey
    val id:Long = 0L,
    val name: String,
    val counter: Int = 0,
    val updated: Date = Date(),
    //if parent id is null, this item considered to be top node
    val parentId:String? = null
)