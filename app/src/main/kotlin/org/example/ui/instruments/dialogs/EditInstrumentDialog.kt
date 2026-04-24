package org.example.ui.instruments.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
        shape = RoundedCornerShape(0.dp),
        title = { Text("Edit Instrument", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppTextField(value = name, onValueChange = { name = it }, label = "Name")
                AppTextField(value = instrument.type.name.replace("_", " "), onValueChange = {}, label = "Type", enabled = false)
                AppTextField(value = location, onValueChange = { location = it }, label = "Location")
                AppTextField(value = inventory, onValueChange = { inventory = it }, label = "Inventory number (optional)")
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, instrument.type, location, inventory.ifBlank { null }) },
                enabled = name.isNotBlank() && location.isNotBlank(),
                shape = RoundedCornerShape(0.dp)
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = RoundedCornerShape(0.dp)) { Text("Cancel") }
        }
    )
}
