package org.example.ui.instruments.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConnectionDialog(
    currentUrl: String,
    currentUser: String,
    currentPassword: String,
    onConfirm: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var url by remember { mutableStateOf(currentUrl) }
    var user by remember { mutableStateOf(currentUser) }
    var password by remember { mutableStateOf(currentPassword) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(0.dp),
        title = { Text("Database Connection", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppTextField(value = url, onValueChange = { url = it }, label = "URL")
                AppTextField(value = user, onValueChange = { user = it }, label = "User")
                AppTextField(value = password, onValueChange = { password = it }, label = "Password")
                Text(
                    "Example: jdbc:postgresql://192.168.1.100:5432/putao",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(url, user, password) },
                enabled = url.isNotBlank() && user.isNotBlank(),
                shape = RoundedCornerShape(0.dp)
            ) { Text("Connect") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = RoundedCornerShape(0.dp)) { Text("Cancel") }
        }
    )
}