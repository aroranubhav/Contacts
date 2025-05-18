package com.maxi.contacts

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.maxi.contacts.presentation.screens.ContactsScreen
import com.maxi.contacts.presentation.ui.theme.ContactsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactsTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    topBar = {
                        AppBar()
                    }
                ) { innerPadding ->
                    App(
                        Modifier
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar() {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.app_name))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.surface
        ),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    )
}

@Composable
fun App(modifier: Modifier) {
    val context = LocalContext.current
    ContactsScreen(modifier,
        onCallClicked = { number ->
            launchCaller(context, number)
        }, onEmailClicked = { email ->
            launchEmailClient(context, email)
        })
}

private fun launchCaller(context: Context, number: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        setData(Uri.parse("tel:$number"))
    }
    context.startActivity(intent)
}

private fun launchEmailClient(context: Context, email: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        setData(Uri.parse("mailto:$email"))
    }
    context.startActivity(intent)
}
