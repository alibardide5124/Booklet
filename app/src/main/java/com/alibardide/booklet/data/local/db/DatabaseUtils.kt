package com.alibardide.booklet.data.local.db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

interface ContentValuesUpdater<T> {
    fun updateContentValues(entity: T, contentValues: ContentValues)
}

interface CursorConverter<T> {
    fun cursorToList(cursor: Cursor, db: SQLiteDatabase) : List<T>
}