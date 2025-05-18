package com.maxi.contacts.data.repository

import android.content.Context
import android.provider.ContactsContract
import com.maxi.contacts.domain.model.Contact
import com.maxi.contacts.domain.repository.ContactsRepository
import com.maxi.contacts.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider,
) : ContactsRepository {

    override suspend fun getContacts(): Flow<List<Contact>> {
        val contactsList = flowOf(getContactNames())
        val contactsNumbers = flowOf(getContactsNumbers())
        val contactsEmails = flowOf(getContactEmailAddresses())
        return contactsList.zip(contactsNumbers) { contacts: List<Contact>, numbers: MutableMap<String, ArrayList<String>> ->
            contacts.forEach { contact ->
                numbers[contact.id]?.let { numbers ->
                    contact.numbers = numbers
                }
            }
            return@zip contacts
        }
            .zip(contactsEmails) { contacts: List<Contact>, emails: MutableMap<String, ArrayList<String>> ->
                contacts.forEach { contact ->
                    emails[contact.id]?.let { emails ->
                        contact.emailIds = emails
                    }
                }
                return@zip contacts
            }
    }

    private suspend fun getContactNames(): List<Contact> {
        val contacts = arrayListOf<Contact>()
        val job = CoroutineScope(dispatcherProvider.io).async {
            context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )?.use { cursor ->
                val idIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getString(idIdx)
                    val name = cursor.getString(nameIdx)
                    name?.let {
                        contacts.add(
                            Contact(
                                id = id,
                                name = name
                            )
                        )
                    }
                }
                cursor.close()
            }
        }
        job.await()
        return contacts.distinct()
    }

    private suspend fun getContactsNumbers(): MutableMap<String, ArrayList<String>> {
        val contactMap = mutableMapOf<String, ArrayList<String>>()
        val job = CoroutineScope(dispatcherProvider.io).async {
            context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )?.use { cursor ->
                val idIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val numberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (cursor.moveToNext()) {
                    val contactId = cursor.getString(idIdx)
                    val number = cursor.getString(numberIdx)
                    if (contactMap.containsKey(contactId)) {
                        if (!contactMap[contactId]!!.contains(getNonWhiteSpacedNumber(number))) {
                            contactMap[contactId]!!.add(number)
                        }
                    } else {
                        contactMap[contactId] = arrayListOf(getNonWhiteSpacedNumber(number))
                    }
                }
                cursor.close()
            }
        }
        job.await()
        return contactMap
    }

    private suspend fun getContactEmailAddresses(): MutableMap<String, ArrayList<String>> {
        val emailMap = mutableMapOf<String, ArrayList<String>>()
        val job = CoroutineScope(dispatcherProvider.io).async {
            context.contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                null,
                null,
                null
            )?.use { cursor ->
                val idIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)

                while (cursor.moveToNext()) {
                    val contactId = cursor.getString(idIdx)
                    val email = cursor.getString(emailIdx)
                    if (emailMap.containsKey(contactId)) {
                        emailMap[contactId]!!.add(email)
                    } else {
                        emailMap[contactId] = arrayListOf(email)
                    }
                }
                cursor.close()
            }
        }
        job.await()
        return emailMap
    }

    private fun getNonWhiteSpacedNumber(number: String): String {
        return number.filterNot {
            it.isWhitespace()
        }
        /*return number.filter {
            it.code !in 0..9
        }*/
    }
}