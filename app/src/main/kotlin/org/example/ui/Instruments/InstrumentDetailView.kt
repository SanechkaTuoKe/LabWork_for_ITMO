package org.example.ui.Instruments

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.example.domain.Calibration
import org.example.domain.Instrument
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
    Column(modifier = Modifier.padding(8.dp)) {
        if (selected != null) {
            Text("DETAILS", style = MaterialTheme.typography.titleMedium)
            Text("")
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

            Text("")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEdit) { Text("Edit") }
                Button(onClick = onCalibrate) { Text("Calibration") }
                Button(onClick = onMaintain) { Text("Maintenance") }
                Button(onClick = onDelete) { Text("Delete") }
            }

            Text("")

            Text("Calibrations:", style = MaterialTheme.typography.titleSmall)
            if (calibrations.isEmpty()) {
                Text("No calibrations")
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                    items(calibrations) { cal ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                            Column(modifier = Modifier.padding(4.dp)) {
                                Text("${cal.type}: ${cal.result} at ${cal.calibratedAt}")
                                cal.comment?.let { Text("Comment: $it") }
                            }
                        }
                    }
                }
            }

            Text("")

            Text("Maintenances:", style = MaterialTheme.typography.titleSmall)
            if (maintenances.isEmpty()) {
                Text("No maintenances")
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                    items(maintenances) { maint ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                            Column(modifier = Modifier.padding(4.dp)) {
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