package com.maxi.contacts.data.repository

import android.content.Context
import android.provider.ContactsContract
import com.maxi.contacts.domain.model.Contact
import com.maxi.contacts.domain.repository.ContactsRepository
import com.maxi.contacts.utils.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider,
) : ContactsRepository {
    override suspend fun getContacts(): Flow<List<Contact>> {
        return flow {
            emit(fetchContactsFromProvider())
        }
            .flowOn(dispatcherProvider.io)
    }

    private fun fetchContactsFromProvider(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME
        )?.use { cursor ->
            val idIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)

            while (cursor.moveToNext()) {
                cursor.apply {
                    contacts.add(
                        Contact(
                            getString(idIdx),
                            getString(nameIdx),
                            getString(numberIdx),
                            getString(emailIdx)
                        )
                    )
                }
            }
        }
        return contacts
    }
}