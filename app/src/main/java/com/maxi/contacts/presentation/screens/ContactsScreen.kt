@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

package com.maxi.contacts.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    modifier: Modifier,
    onCallClicked: (number: String) -> Unit,
    onEmailClicked: (email: String) -> Unit
) {
    val viewModel: ContactsViewModel = viewModel()
    val state = viewModel.uiState.collectAsState()
    val contacts = viewModel.filteredContacts.collectAsState(initial = mutableMapOf())

    when (state.value) {
        is UiState.Success -> {
            LoadContacts(modifier, viewModel, contacts.value, onCallClicked, onEmailClicked)
        }

        is UiState.Error -> {
            val error = (state.value as UiState.Error).error
            println("Error occurred while loading contacts -- $error")
        }

        is UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(1f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(true) {
                    LinearProgressIndicator(
                        modifier
                            .fillMaxWidth(.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun LoadContacts(
    modifier: Modifier,
    viewModel: ContactsViewModel,
    contacts: Map<String, List<Contact>>,
    onCallClicked: (number: String) -> Unit,
    onEmailClicked: (email: String) -> Unit
) {

    val searchQuery by viewModel.queryString.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        //search bar
        SearchField(
            searchQuery = searchQuery,
            onQueryChanged = viewModel::onQueryTextChanged,
            modifier = modifier
        )
        //lazy column
        LazyColumn(
            modifier
                .fillMaxSize()
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
                    ContactListItem(entry.value[it], onCallClicked, onEmailClicked)
                }
            }
        }
    }
}

@Composable
fun SearchField(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = searchQuery,
        onValueChange = onQueryChanged,
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(MaterialTheme.shapes.extraSmall)
            .indicatorLine(
                enabled = false,
                isError = false,
                interactionSource = remember {
                    MutableInteractionSource()
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                focusedIndicatorLineThickness = 0.dp,
                unfocusedIndicatorLineThickness = 0.dp
            ),
        placeholder = { Text(text = "Search") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = ""
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            focusedTextColor = Color.Black,
            unfocusedContainerColor = Color.White,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.Green,
        )
    )
}

@Composable
fun ContactListItem(
    contact: Contact,
    onCallClicked: (number: String) -> Unit,
    onEmailClicked: (email: String) -> Unit
) {
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
                var numbers = ""
                contact.numbers.forEach { number ->
                    numbers += "$number\n"
                }
                Text(
                    text = numbers,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                IconButton(
                    onClick = {
                        onCallClicked(contact.numbers[0])
                    }
                ) {
                    Image(
                        imageVector = Icons.Default.Call,
                        contentDescription = null
                    )
                }
            }
            if (contact.emailIds.size > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var emailIds = ""
                    contact.emailIds.forEach { email ->
                        emailIds += "$email\n"
                    }
                    Text(
                        text = emailIds,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    IconButton(
                        onClick = {
                            onEmailClicked(contact.emailIds[0])
                        }
                    ) {
                        Image(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewContactListItem() {
    ContactListItem(
        Contact(
            id = "",
            name = "Wtf",
            numbers = arrayListOf("03249824", "340584390", "234895"),
            emailIds = arrayListOf("wtf@wtf.in")
        ), {}, {}
    )
}