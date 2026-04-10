package org.example.ui.instruments.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import org.example.domain.InstrumentType

@Composable
fun AddInstrumentDialog(
    onConfirm: (String, InstrumentType, String, String?) -> Unit,
    onDismiss: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var inventory by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(InstrumentType.PH_METER) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Instrument") },
        text = {
            Column {
                OutlinedTextField(name, { name = it }, label = { Text("Name") })
                OutlinedTextField(location, { location = it }, label = { Text("Location") })
                OutlinedTextField(inventory, { inventory = it }, label = { Text("Inventory") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank() && location.isNotBlank()) {
                    onConfirm(name, type, location, inventory.ifBlank { null })
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