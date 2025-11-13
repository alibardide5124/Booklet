package com.phoenix.booklet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.phoenix.booklet.data.model.Book

@Dao
interface BookDao {

    @Insert
    fun insertBook(book: Book)

    @Update
    fun updateBook(book: Book)

    @Query("SELECT * FROM books")
    fun getAllBooks(): List<Book>

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookDetails(id: Int): Book

}