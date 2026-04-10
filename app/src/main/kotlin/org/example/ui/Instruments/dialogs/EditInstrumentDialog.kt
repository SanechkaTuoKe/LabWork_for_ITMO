package org.example.ui.Instruments.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import org.example.domain.Instrument
import org.example.domain.InstrumentType

@Composable
fun EditInstrumentDialog(
    instrument: Instrument,
    onConfirm: (String, InstrumentType, String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(instrument.name) }
    var location by remember { mutableStateOf(instrument.location) }
    var inventory by remember { mutableStateOf(instrument.inventoryNumber ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Instrument") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )

                OutlinedTextField(
                    value = instrument.type.name,
                    onValueChange = {},
                    label = { Text("Type") },
                    enabled = false
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") }
                )

                OutlinedTextField(
                    value = inventory,
                    onValueChange = { inventory = it },
                    label = { Text("Inventory (optional)") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(name, instrument.type, location, inventory.ifBlank { null })
                },
                enabled = name.isNotBlank() && location.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}