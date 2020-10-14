package com.alibardide.booklet.data.local.db.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.alibardide.booklet.data.local.db.AppDatabase
import com.alibardide.booklet.data.model.Book

class BookDao(private val appDatabase: AppDatabase) : BaseDao<Book>(appDatabase) {

    override fun save(entity: Book): Boolean {
        val db = appDatabase.writableDatabase
        updateContentValues(entity, contentValues)
        val result = db.insert(AppDatabase.TABLE_BOOK, null, contentValues)
        db.close()
        return result > 0
    }

    override fun save(id: String, entity: Book): Boolean {
        val db = appDatabase.writableDatabase
        updateContentValues(entity, contentValues)
        val result = db.update(AppDatabase.TABLE_BOOK, contentValues, "${AppDatabase.BOOK_ID} = ?", arrayOf(id))
        db.close()
        return result > 0
    }

    override fun findAll(): List<Book> {
        val db = appDatabase.readableDatabase
        query = "SELECT * FROM ${AppDatabase.TABLE_BOOK}"
        val cursor = db.rawQuery(query, null)
        return cursorToList(cursor, db)
    }

    override fun find(columnName: String, columnValue: String): List<Book> {
        val db = appDatabase.readableDatabase
        query = "SELECT * FROM ${AppDatabase.TABLE_BOOK} WHERE $columnName = ?"
        val cursor = db.rawQuery(query, arrayOf(columnValue))
        return cursorToList(cursor, db)
    }

    override fun delete(id: String): Boolean {
        val db = appDatabase.writableDatabase
        val result = db.delete(AppDatabase.TABLE_BOOK, "${AppDatabase.BOOK_ID} = ?", arrayOf(id))
        db.close()
        return result > 0
    }

    override fun updateContentValues(entity: Book, contentValues: ContentValues) {
        contentValues.clear()
        contentValues.put(AppDatabase.BOOK_NAME, entity.name)
        contentValues.put(AppDatabase.BOOK_AUTHOR, entity.author)
        contentValues.put(AppDatabase.BOOK_PUBLISHER, entity.publisher)
        contentValues.put(AppDatabase.BOOK_DESCRIPTION, entity.description)
        contentValues.put(AppDatabase.BOOK_CATEGORY, entity.category)
        contentValues.put(AppDatabase.BOOK_DATE, entity.date)
        contentValues.put(AppDatabase.BOOK_PAGES, entity.pages)
        contentValues.put(AppDatabase.BOOK_PROGRESS, entity.progress)
        contentValues.put(AppDatabase.BOOK_STATE, entity.state)
        contentValues.put(AppDatabase.BOOK_PIC_LOCATION, entity.picLocation)
    }

    override fun cursorToList(cursor: Cursor, db: SQLiteDatabase): List<Book> {
        data.clear()
        if (cursor.moveToFirst()) {
            do {
                data.add(Book(
                    cursor.getString(cursor.getColumnIndex(AppDatabase.BOOK_ID)).toLong(),
                    cursor.getString(cursor.getColumnIndex(AppDatabase.BOOK_NAME)),
                    cursor.getString(cursor.getColumnIndex(AppDatabase.BOOK_AUTHOR)),
                    cursor.getString(cursor.getColumnIndex(AppDatabase.BOOK_PUBLISHER)),
                    cursor.getString(cursor.getColumnIndex(AppDatabase.BOOK_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(AppDatabase.BOOK_CATEGORY)),
                    cursor.getString(cursor.getColumnIndex(AppDatabase.BOOK_DATE)),
                    cursor.getInt(cursor.getColumnIndex(AppDatabase.BOOK_PAGES)),
                    cursor.getInt(cursor.getColumnIndex(AppDatabase.BOOK_PROGRESS)),
                    cursor.getInt(cursor.getColumnIndex(AppDatabase.BOOK_STATE)),
                    cursor.getString(cursor.getColumnIndex(AppDatabase.BOOK_PIC_LOCATION))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return data
    }
}