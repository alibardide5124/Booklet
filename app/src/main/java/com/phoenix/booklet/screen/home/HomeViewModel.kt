package com.phoenix.booklet.screen.home

import androidx.lifecycle.ViewModel
import com.phoenix.booklet.data.dao.BookDao
import com.phoenix.booklet.data.model.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val bookDao: BookDao
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _books = MutableStateFlow(emptyList<Book>())
    val books = _books.asStateFlow()

    fun onAction(action: HomeUiActions) {
        when(action) {
            HomeUiActions.DismissDialog ->
                _uiState.update { it.copy(dialogType = HomeDialog.None) }

            HomeUiActions.InsertBook ->
                _uiState.update { it.copy(dialogType = HomeDialog.Insert) }

            is HomeUiActions.UpdateBook ->
                _uiState.update { it.copy(dialogType = HomeDialog.Update(action.book)) }

        }
    }

}