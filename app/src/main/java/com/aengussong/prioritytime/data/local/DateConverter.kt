package com.aengussong.prioritytime.data.local

import androidx.room.TypeConverter
import java.util.*


class DateConverter {

    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time

    @TypeConverter
    fun longToDate(long: Long): Date = Date(long)
}