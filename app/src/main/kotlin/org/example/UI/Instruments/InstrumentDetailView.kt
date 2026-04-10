package org.example.ui.instruments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import org.example.domain.Instrument
import org.example.domain.Calibration
import org.example.domain.Maintenance

@Composable
fun InstrumentDetailView(
    selected: Instrument?,
    calibrations: List<Calibration>,
    maintenances: List<Maintenance>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCalibrate: () -> Unit,
    onMaintain: () -> Unit
) {
    Column {
        if (selected != null) {
            Text("DETAILS", style = MaterialTheme.typography.titleMedium)
            Text("") // пустая строка вместо отступа
            Text("ID: ${selected.id}")
            Text("Name: ${selected.name}")
            Text("Type: ${selected.type}")
            Text("Location: ${selected.location}")
            Text("Status: ${selected.status}")
            Text("Owner: ${selected.ownerUsername}")
            Text("Created: ${selected.createdAt}")
            Text("Updated: ${selected.updatedAt}")
            if (selected.inventoryNumber != null) {
                Text("Inventory: ${selected.inventoryNumber}")
            }

            Text("") // пустая строка

            Row {
                Button(onClick = onEdit) { Text("Edit") }
                Button(onClick = onCalibrate) { Text("Calibration") }
                Button(onClick = onMaintain) { Text("Maintenance") }
                Button(onClick = onDelete) { Text("Delete") }
            }

            Text("") // пустая строка

            // Список калибровок
            Text("Calibrations:", style = MaterialTheme.typography.titleSmall)
            if (calibrations.isEmpty()) {
                Text("No calibrations")
            } else {
                LazyColumn {
                    items(calibrations) { cal ->
                        Card {
                            Column {
                                Text("${cal.type}: ${cal.result} at ${cal.calibratedAt}")
                                cal.comment?.let { Text("Comment: $it") }
                            }
                        }
                    }
                }
            }

            Text("") // пустая строка

            // Список обслуживаний
            Text("Maintenances:", style = MaterialTheme.typography.titleSmall)
            if (maintenances.isEmpty()) {
                Text("No maintenances")
            } else {
                LazyColumn {
                    items(maintenances) { maint ->
                        Card {
                            Column {
                                Text("${maint.type} at ${maint.doneAt}")
                                Text(maint.details)
                            }
                        }
                    }
                }
            }
        } else {
            Text("No instrument selected")
        }
    }
}