package com.example.yadro_test_3.presentation.view

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.yadro_test_3.presentation.ContactViewModel
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen(viewModel: ContactViewModel) {

    val groupedContacts by viewModel.groupedContacts.collectAsState(emptyMap())
    val status by viewModel.status.collectAsState()
    val context = LocalContext.current
    var contacts by remember { mutableStateOf(emptySet<String>()) }
    val permissionEvent by viewModel.requestPermissionsEvent.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        viewModel.onPermissionResult(results)
    }

    LaunchedEffect(permissionEvent) {
        permissionEvent?.let { permissions ->
            permissionLauncher.launch(permissions)
        }
    }

    LaunchedEffect(Unit) {
        val read = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        val write = ContextCompat.checkSelfPermission(
            context, Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        viewModel.checkPermissionsAndLoadContacts(hasReadPermission = read, hasWritePermission = write)
    }

    contacts = groupedContacts.values.flatten().map { it.id }.toSet()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp, bottom = 50.dp, start = 16.dp, end = 16.dp)
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            groupedContacts.forEach { (letter, contactsForLetter) ->
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(start = 12.dp, top = 6.dp, bottom = 6.dp)
                    ) {
                        Text(
                            text = letter,
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 20.sp
                            ),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                items(
                    items = contactsForLetter,
                    key = { it.id }
                ) { contact ->
                    AnimatedVisibility(
                        visible = contacts.contains(contact.id),
                        exit = fadeOut(animationSpec = tween(300)) +
                                shrinkVertically(animationSpec = tween(300)),
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                    ) {
                        ContactItem(contact = contact)
                    }
                }
            }
        }


        Button(
            onClick = { viewModel.deleteDuplicates() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Delete duplicate contacts")
        }

        status?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearStatusMessage()
            }
        }
    }
}
