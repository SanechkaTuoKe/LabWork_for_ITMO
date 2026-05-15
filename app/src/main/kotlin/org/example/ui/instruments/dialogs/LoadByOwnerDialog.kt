package org.example.ui.instruments.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.auth.UserService

@Composable
fun LoadByOwnerDialog(
    userService: UserService,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var users by remember { mutableStateOf<List<String>>(emptyList()) }
    var selected by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        users = withContext(Dispatchers.IO) { userService.getAllUsers() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Load by Owner") },
        text = {
            Column {
                users.forEach { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected = user }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected == user,
                            onClick = { selected = user }
                        )
                        Text(user)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { selected?.let { onSelect(it) } },
                enabled = selected != null
            ) { Text("Load") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}