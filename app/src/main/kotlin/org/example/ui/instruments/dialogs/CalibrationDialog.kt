package org.example.ui.instruments.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import org.example.domain.CalibrationResult
import org.example.domain.CalibrationType

@Composable
fun CalibrationDialog(
    onConfirm: (String, String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var type by remember { mutableStateOf<CalibrationType?>(null) }
    var result by remember { mutableStateOf<CalibrationResult?>(null) }
    var comment by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }
    var resultExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(0.dp),
        title = { Text("Add Calibration", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppDropdown(
                    label = "Type",
                    selected = type?.name?.replace("_", " "),
                    options = CalibrationType.values().map { it.name.replace("_", " ") },
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it },
                    onSelect = { idx -> type = CalibrationType.values()[idx]; typeExpanded = false }
                )
                AppDropdown(
                    label = "Result",
                    selected = result?.name,
                    options = CalibrationResult.values().map { it.name },
                    expanded = resultExpanded,
                    onExpandedChange = { resultExpanded = it },
                    onSelect = { idx -> result = CalibrationResult.values()[idx]; resultExpanded = false }
                )
                AppTextField(value = comment, onValueChange = { comment = it }, label = "Comment (optional)")
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(type!!.name, result!!.name, comment.ifBlank { null }) },
                enabled = type != null && result != null,
                shape = RoundedCornerShape(0.dp)
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = RoundedCornerShape(0.dp)) { Text("Cancel") }
        }
    )
}
