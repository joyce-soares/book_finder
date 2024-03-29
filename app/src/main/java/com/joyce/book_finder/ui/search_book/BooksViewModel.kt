package com.joyce.book_finder.ui.search_book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joyce.book_finder.models.ResponseGetBooks
import com.joyce.book_finder.network.RetrofitService
import com.joyce.book_finder.request.RequestHandler
import com.joyce.book_finder.request.then
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BooksViewModel(
    private val service: RetrofitService,
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean>
        get() = _loading

    private var _state = MutableStateFlow<BooksState?>(null)
    val state: StateFlow<BooksState?>
        get() = _state

    fun getAllBooks(texto: String) {
        _loading.value = true
        viewModelScope.launch {
            RequestHandler.doRequest { service.getAllBooks(texto) }.then(
                onSuccess = {
                    _loading.value = false
                    _state.value = BooksState.Success(it)
                },
                onError = {
                    _loading.value = false
                    _state.value = BooksState.Error(it)
                })
        }
    }
}

sealed class BooksState {
    class Success(var books: ResponseGetBooks) : BooksState()
    class Error(val message: String) : BooksState()
}