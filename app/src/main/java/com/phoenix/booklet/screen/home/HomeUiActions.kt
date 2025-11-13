package com.phoenix.booklet.screen.home

import com.phoenix.booklet.data.model.Book

sealed interface HomeUiActions {
    data object DismissDialog: HomeUiActions
    data object InsertBookDialog: HomeUiActions
    data class UpdateBookDialog(val book: Book): HomeUiActions
    data class InsertBook(val book: Book): HomeUiActions
    data class UpdateBook(val book: Book): HomeUiActions
}