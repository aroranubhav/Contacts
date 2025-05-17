package com.maxi.contacts.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxi.contacts.domain.model.Contact
import com.maxi.contacts.domain.usecase.ContactsUseCase
import com.maxi.contacts.utils.DispatcherProvider
import com.maxi.contacts.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val useCase: ContactsUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Map<String, List<Contact>>>>(UiState.Loading)
    val uiState: StateFlow<UiState<Map<String, List<Contact>>>>
        get() = _uiState

    init {
        getContacts()
    }

    private fun getContacts() {
        viewModelScope.launch {
            useCase()
                .flowOn(dispatcherProvider.io)
                .catch { e ->
                    _uiState.value = UiState.Error(e.message.toString())
                }
                .collect { contactsMap ->
                    _uiState.value = UiState.Success(contactsMap)
                }
        }
    }
}