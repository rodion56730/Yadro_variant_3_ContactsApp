package com.example.yadro_test_3.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.yadro_test_3.aidl.Contact

@Composable
fun ContactItem(contact: Contact) {
    val photoUri = remember(contact.avatar) {
        contact.avatar?.toUri()
    }
    Row(modifier = Modifier.padding(8.dp)) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Contact photo",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Default avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(text = contact.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = contact.phone, style = MaterialTheme.typography.bodySmall)
        }
    }
}
