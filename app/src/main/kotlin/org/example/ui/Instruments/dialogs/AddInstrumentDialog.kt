package org.example.ui.Instruments.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
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
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Instrument") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )

                // Выпадающий список выбора типа
                Box {
                    OutlinedTextField(
                        value = type.name,
                        onValueChange = {},
                        label = { Text("Type") },
                        readOnly = true
                    )
                    IconButton(
                        onClick = { expanded = true }
                    ) {
                        Text("▼")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        InstrumentType.values().forEach { instrumentType ->
                            DropdownMenuItem(
                                text = { Text(instrumentType.name) },
                                onClick = {
                                    type = instrumentType
                                    expanded = false
                                }
                            )
                        }
                    }
                }

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
                    onConfirm(name, type, location, inventory.ifBlank { null })
                },
                enabled = name.isNotBlank() && location.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}