package org.example.ui.Instruments.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CalibrationDialog(
    onConfirm: (String, String, String?) -> Unit,
    onDismiss: () -> Unit
) {

    var type by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Calibration") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type (ONE_POINT/TWO_POINT)") }
                )

                OutlinedTextField(
                    value = result,
                    onValueChange = { result = it },
                    label = { Text("Result (OK/FAIL)") }
                )

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comment (optional)") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(type, result, comment)
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