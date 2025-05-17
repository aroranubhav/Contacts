@file:OptIn(ExperimentalFoundationApi::class)

package com.maxi.contacts.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maxi.contacts.domain.model.Contact
import com.maxi.contacts.presentation.viewmodel.ContactsViewModel
import com.maxi.contacts.utils.UiState

@Composable
fun ContactsScreen(
    modifier: Modifier
) {
    val viewModel: ContactsViewModel = viewModel()
    val state = viewModel.uiState.collectAsState()

    when (state.value) {
        is UiState.Success -> {
            val contacts = (state.value as UiState.Success).data
            LoadContacts(modifier, contacts)
        }

        is UiState.Error -> {
            val error = (state.value as UiState.Error).error
            println("Error occurred while loading contacts -- $error")
        }

        is UiState.Loading -> {
            LinearProgressIndicator(
                modifier
                    .fillMaxSize(.6f)
            )
            /*AnimatedVisibility(true) {
                LinearProgressIndicator(
                    modifier
                        .fillMaxWidth(.6f)
                )
            }*/
        }
    }
}

@Composable
fun LoadContacts(modifier: Modifier, contacts: Map<String, List<Contact>>) {
    LazyColumn(
        modifier
            .padding(6.dp)
    ) {
        contacts.map { entry ->
            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp)
                ) {
                    Text(
                        text = entry.key,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            items(entry.value.size) {
                ContactListItem(entry.value[it])
            }
        }
    }
}

@Composable
fun ContactListItem(contact: Contact) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults
            .cardElevation(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text(
                text = contact.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = contact.number,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Image(
                    imageVector = Icons.Default.Call,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewContactListItem() {
    ContactListItem(Contact(id = "", name = "Wtf", number = "03249824", email = "wtf@wtf.com"))
}