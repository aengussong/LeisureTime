package com.aengussong.prioritytime.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity
data class LeisureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val counter: Long = 0,
    val updated: Date = Date(),
    /**
     * ancestry written in ids sequence with slash as delimiter, e.g. "ROOT/2/5/"
     * that means that current entity is child for entity with id 5, which, in his turn, child of
     * entity with id 2
     */
    val ancestry: String
) : Parcelable {
    constructor(leisureName: String, leisureCount: Long, leisureAncestry: String) : this(
        name = leisureName,
        counter = leisureCount,
        ancestry = leisureAncestry
    )
}