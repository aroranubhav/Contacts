package com.maxi.contacts.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxi.contacts.domain.model.Contact
import com.maxi.contacts.domain.usecase.ContactsUseCase
import com.maxi.contacts.utils.DispatcherProvider
import com.maxi.contacts.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val useCase: ContactsUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Map<String, List<Contact>>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _queryString = MutableStateFlow("")
    val queryString = _queryString.asStateFlow()

    private var _contacts = MutableStateFlow<List<Contact>>(emptyList())

    init {
        getContacts()
    }

    val filteredContacts = queryString
        .combine(_contacts) { query, contacts ->
            if (query.isBlank()) {
                mapListToContactMap(contacts)
            } else {
                mapListToContactMap(contacts.filter {
                    it.name.contains(query, ignoreCase = true)
                })
            }
        }

    private fun getContacts() {
        viewModelScope.launch {
            useCase()
                .flowOn(dispatcherProvider.io)
                .catch { e ->
                    _uiState.value = UiState.Error(e.message.toString())
                }
                .collect { contacts ->
                    _uiState.value = UiState.Success(mapListToContactMap(contacts))
                    _contacts.value = contacts
                }
        }
    }

    private fun mapListToContactMap(contacts: List<Contact>): Map<String, List<Contact>> {
        return contacts.groupBy { contact ->
            contact.name.uppercase().first().toString()
        }
    }

    fun onQueryTextChanged(query: String) {
        _queryString.value = query
    }
}