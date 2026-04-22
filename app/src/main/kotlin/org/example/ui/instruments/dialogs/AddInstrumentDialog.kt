package org.example.ui.instruments.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.domain.InstrumentType
import org.example.ui.theme.*

@Composable
fun AddInstrumentDialog(
    onConfirm: (String, InstrumentType, String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var inventory by remember { mutableStateOf("") }
    var type by remember { mutableStateOf<InstrumentType?>(null) }
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(0.dp),
        title = { Text("Add Instrument", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppTextField(value = name, onValueChange = { name = it }, label = "Name")
                AppDropdown(
                    label = "Type",
                    selected = type?.name?.replace("_", " "),
                    options = InstrumentType.values().map { it.name.replace("_", " ") },
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it },
                    onSelect = { idx ->
                        type = InstrumentType.values()[idx]
                        typeExpanded = false
                    }
                )

                AppTextField(value = location, onValueChange = { location = it }, label = "Location")
                AppTextField(value = inventory, onValueChange = { inventory = it }, label = "Inventory number (optional)")
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, type!!, location, inventory.ifBlank { null }) },
                enabled = name.isNotBlank() && location.isNotBlank() && type != null,
                shape = RoundedCornerShape(0.dp)
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = RoundedCornerShape(0.dp)) { Text("Cancel") }
        }
    )
}
