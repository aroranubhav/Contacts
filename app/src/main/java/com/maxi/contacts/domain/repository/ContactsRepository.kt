package com.maxi.contacts.domain.repository

import com.maxi.contacts.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {

    suspend fun getContacts(): Flow<List<Contact>>
}