package com.alibardide.booklet.data.local.db.dao

import android.content.ContentValues
import com.alibardide.booklet.data.local.db.AppDatabase
import com.alibardide.booklet.data.local.db.ContentValuesUpdater
import com.alibardide.booklet.data.local.db.CursorConverter

abstract class BaseDao<T>(val database: AppDatabase): ContentValuesUpdater<T>, CursorConverter<T> {
    val contentValues = ContentValues()
    var query = ""
    val data: MutableList<T> = ArrayList()

    abstract fun save(entity: T) : Boolean
    abstract fun save(id: String, entity: T) : Boolean
    abstract fun findAll() : List<T>
    abstract fun find(columnName: String, columnValue: String) : List<T>
    abstract fun delete(id: String) : Boolean
}