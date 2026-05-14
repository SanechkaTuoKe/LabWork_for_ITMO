package org.example.ui.instruments.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import javax.swing.JFileChooser
import javax.swing.SwingUtilities

@Composable
fun FilePickerDialog(
    title: String,
    confirmLabel: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPath by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(0.dp),
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Select a folder",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (selectedPath != null) {
                    Text(
                        text = "Selected: ${selectedPath!!}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        SwingUtilities.invokeLater {
                            val chooser = JFileChooser().apply {
                                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                                dialogTitle = title
                            }
                            val result = chooser.showOpenDialog(null)
                            if (result == JFileChooser.APPROVE_OPTION) {
                                selectedPath = chooser.selectedFile.absolutePath
                            }
                        }
                    },
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("Browse...")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val path = selectedPath
                    if (path != null) {
                        onConfirm(path)
                    }
                },
                enabled = selectedPath != null,
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = RoundedCornerShape(0.dp)) {
                Text("Cancel")
            }
        }
    )
}