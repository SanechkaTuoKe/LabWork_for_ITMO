package org.example.ui.instruments.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import org.example.domain.MaintenanceType

@Composable
fun MaintenanceDialog(
    onConfirm: (MaintenanceType, String) -> Unit,
    onDismiss: () -> Unit
) {
    var type by remember { mutableStateOf<MaintenanceType?>(null) }
    var details by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(0.dp),
        title = { Text("Add Maintenance", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppDropdown(
                    label = "Type",
                    selected = type?.name,
                    options = MaintenanceType.values().map { it.name },
                    onSelect = { idx -> type = MaintenanceType.values()[idx]}
                )
                AppTextField(value = details, onValueChange = { details = it }, label = "Details")
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(type!!, details) },
                enabled = type != null && details.isNotBlank(),
                shape = RoundedCornerShape(0.dp)
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = RoundedCornerShape(0.dp)) { Text("Cancel") }
        }
    )
}
