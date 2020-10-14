package com.alibardide.booklet.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "booklet.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_BOOK = "book"
        const val BOOK_ID = "id"
        const val BOOK_NAME = "name"
        const val BOOK_AUTHOR = "author"
        const val BOOK_PUBLISHER = "publisher"
        const val BOOK_DESCRIPTION = "description"
        const val BOOK_CATEGORY = "category"
        const val BOOK_DATE = "date"
        const val BOOK_PAGES = "pages"
        const val BOOK_PROGRESS = "progress"
        const val BOOK_STATE = "state"
        const val BOOK_PIC_LOCATION = "picLocation"

    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_BOOK (" +
                "$BOOK_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$BOOK_NAME TEXT," +
                "$BOOK_AUTHOR TEXT, " +
                "$BOOK_PUBLISHER TEXT, " +
                "$BOOK_DESCRIPTION TEXT, " +
                "$BOOK_CATEGORY TEXT, " +
                "$BOOK_DATE DATE, " +
                "$BOOK_PAGES INTEGER, " +
                "$BOOK_PROGRESS INTEGER, " +
                "$BOOK_STATE INTEGER," +
                "$BOOK_PIC_LOCATION TEXT);")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $TABLE_BOOK")
        onCreate(p0)
    }

}