package com.phoenix.booklet.data

import androidx.room.TypeConverter
import com.phoenix.booklet.data.model.ReadingStatus
import java.util.Date

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromReadingStatus(value: ReadingStatus): String = value.name

    @TypeConverter
    fun stringToReadingStatus(name: String): ReadingStatus = ReadingStatus.valueOf(name)

}