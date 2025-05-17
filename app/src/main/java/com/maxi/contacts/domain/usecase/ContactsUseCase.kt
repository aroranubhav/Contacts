package com.maxi.contacts.domain.usecase

import com.maxi.contacts.domain.model.Contact
import com.maxi.contacts.domain.repository.ContactsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ContactsUseCase @Inject constructor(
    private val repository: ContactsRepository
) {

    suspend operator fun invoke(): Flow<Map<String, List<Contact>>> {
        var contacts = listOf<Contact>()
        repository.getContacts().collect {
            contacts = it
        }
        return flow {
            emit(
                contacts.groupBy { contact ->
                    contact.name.uppercase().first().toString()
                }
            )
        }
    }
}