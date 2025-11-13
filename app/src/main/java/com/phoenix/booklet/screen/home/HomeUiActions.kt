package com.phoenix.booklet.screen.home

import com.phoenix.booklet.data.model.Book

sealed interface HomeUiActions {
    data object DismissDialog: HomeUiActions
    data object InsertBook: HomeUiActions
    data class UpdateBook(val book: Book): HomeUiActions
}