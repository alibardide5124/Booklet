package com.phoenix.booklet.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.phoenix.booklet.utils.DatabaseConstants
import com.phoenix.booklet.data.dao.BookDao
import com.phoenix.booklet.data.model.Book

@Database(
    entities = [Book::class],
    version = DatabaseConstants.DB_VERSION,
    exportSchema = false
)
@TypeConverters(UUIDConverter::class, DateConverter::class, StatusConverter::class)
abstract class AppDatabase(): RoomDatabase() {
    abstract fun bookDao(): BookDao
}