package com.phoenix.booklet.utils

import android.content.Context
import androidx.room.Room
import com.phoenix.booklet.data.AppDatabase
import com.phoenix.booklet.data.dao.BookDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = DatabaseConstants.DB_NAME
        ).build()

    @Provides
    @Singleton
    fun provideBookDao(database: AppDatabase): BookDao =
        database.bookDao()

}