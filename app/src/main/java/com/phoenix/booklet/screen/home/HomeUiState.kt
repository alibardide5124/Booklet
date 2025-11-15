package com.phoenix.booklet.screen.home

import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.data.model.ReadingStatus

data class HomeUiState(
    val dialogType: HomeDialog = HomeDialog.None,
    val selectedFilter: FilterStatus = FilterStatus.ALL,
)

enum class FilterStatus {
    ALL, WISHLIST, READING, FINISHED, ARCHIVED
}

sealed interface HomeDialog {
    data object None: HomeDialog
    data object Insert: HomeDialog
    data class Update(val book: Book): HomeDialog
}