package org.example.ui.instruments.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import org.example.domain.MaintenanceType

@Composable
fun MaintenanceDialog(
    onConfirm: (MaintenanceType, String) -> Unit,
    onDismiss: () -> Unit
) {

    var type by remember { mutableStateOf(MaintenanceType.SERVICE) }
    var details by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Maintenance") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                OutlinedTextField(
                    value = type.name,
                    onValueChange = { },
                    label = { Text("Type (SERVICE/REPAIR)") },
                    enabled = false
                )

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Details") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (details.isNotBlank()) {
                    onConfirm(type, details)
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}