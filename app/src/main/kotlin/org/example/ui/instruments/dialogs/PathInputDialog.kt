package org.example.ui.instruments.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.ui.theme.*

@Composable
fun PathInputDialog(
    title: String,
    hint: String,
    confirmLabel: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var path by remember { mutableStateOf("./data") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(0.dp),
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Enter the directory path where files will be saved/loaded.",
                    style = MaterialTheme.typography.bodySmall
                )
                AppTextField(value = path, onValueChange = { path = it }, label = hint)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(path) },
                enabled = path.isNotBlank(),
                shape = RoundedCornerShape(0.dp)
            ) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = RoundedCornerShape(0.dp)) { Text("Cancel") }
        }
    )
}
