package com.phoenix.booklet.screen.home

import com.phoenix.booklet.data.model.Book

data class HomeUiState(
    val dialogType: HomeDialog = HomeDialog.None,
)

sealed interface HomeDialog {
    data object None: HomeDialog
    data object Insert: HomeDialog
    data class Update(val book: Book): HomeDialog
}