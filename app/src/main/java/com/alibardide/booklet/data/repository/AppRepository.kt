package com.alibardide.booklet.data.repository

import android.util.Log
import com.alibardide.booklet.data.local.db.AppDatabase
import com.alibardide.booklet.data.local.db.dao.BookDao
import com.alibardide.booklet.data.model.Book

class AppRepository(private val bookDao: BookDao) {

    fun saveBook(book: Book) : Boolean {
        return bookDao.save(book)
    }

    fun updateBook(id: String, book: Book) : Boolean {
        return bookDao.save(id, book)
    }

    fun findAllBooks() : List<Book> {
        return bookDao.findAll()
    }

    fun findBookById(id: String) : Book {
        return bookDao.find(AppDatabase.BOOK_ID, id)[0]
    }

    fun findBookByName(name: String) : List<Book> {
        return bookDao.find(AppDatabase.BOOK_NAME, name)
    }

    fun findBookByAuthor(author: String) : List<Book> {
        return bookDao.find(AppDatabase.BOOK_AUTHOR, author)
    }

    fun deleteBook(id: String) : Boolean {
        return bookDao.delete(id)
    }

}