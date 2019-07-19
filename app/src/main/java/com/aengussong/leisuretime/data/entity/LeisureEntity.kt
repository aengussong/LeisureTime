package com.aengussong.leisuretime.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class LeisureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val counter: Int,
    val updated: Date
)