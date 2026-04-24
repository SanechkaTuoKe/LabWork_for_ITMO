package org.example.ui.instruments.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import coil3.compose.LocalPlatformContext
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher

@Composable
fun FilePickerDialog(
    title: String,
    hint: String,
    confirmLabel: String,
    selectionMode: FilePickerSelectionMode = FilePickerSelectionMode.Single,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var path by remember { mutableStateOf("./data") }
    var selectedFileName by remember { mutableStateOf("") }

    val filePickerLauncher = rememberFilePickerLauncher(
        selectionMode = selectionMode,
        onResult = { result ->
            if (result != null) {
                path = result.toString()
                selectedFileName = result.toString()
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(0.dp),
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Enter the path or use the browser to pick one.",
                    style = MaterialTheme.typography.bodySmall
                )

                if (selectedFileName.isNotEmpty()) {
                    Text(
                        "Selected: $selectedFileName",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = path,
                        onValueChange = {
                            path = it
                            if (it != path) selectedFileName = ""
                        },
                        label = { Text(hint) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(0.dp)
                    )
                    TextButton(
                        onClick = {
                            filePickerLauncher.launch()
                        },
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text("Browse...")
                    }
                }
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