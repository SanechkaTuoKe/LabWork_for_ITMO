package org.example.ui.instruments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.domain.Calibration
import org.example.domain.CalibrationResult
import org.example.domain.Instrument
import org.example.domain.Maintenance
import org.example.ui.theme.ColorDivider
import org.example.ui.theme.ColorError
import org.example.ui.theme.ColorOnPrimary
import org.example.ui.theme.ColorPrimary
import org.example.ui.theme.ColorPrimaryVar
import org.example.ui.theme.ColorSurface
import org.example.ui.theme.ColorSurfaceVar
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault())

@Composable
fun InstrumentDetailView(
    selected: Instrument?,
    calibrations: List<Calibration>,
    maintenances: List<Maintenance>,
    currentUsername: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCalibrate: () -> Unit,
    onMaintain: () -> Unit,
    onChangeStatus: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorSurface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorPrimaryVar)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text("Details", style = MaterialTheme.typography.titleSmall, color = ColorOnPrimary)
        }

        if (selected == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Select an instrument", style = MaterialTheme.typography.bodySmall)
            }
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(selected.name, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(selected.type.name.replace("_", " "), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
            Divider(color = ColorDivider)
            Spacer(Modifier.height(12.dp))

            DetailRow("ID", selected.id.toString())
            DetailRow("Location", selected.location)
            DetailRow("Status", selected.status.name)
            DetailRow("Owner", selected.ownerUsername)
            if (selected.inventoryNumber != null) {
                DetailRow("Inventory", selected.inventoryNumber!!)
            }
            DetailRow("Created", fmt.format(selected.createdAt))
            DetailRow("Updated", fmt.format(selected.updatedAt))

            Spacer(Modifier.height(16.dp))

            val isOwner = selected.ownerUsername == currentUsername
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AppButton("Edit", onClick = onEdit, enabled = isOwner)
                AppButton("Calibrate", onClick = onCalibrate)
                AppButton("Maintain", onClick = onMaintain)
                AppButton("Status", onClick = onChangeStatus, enabled = isOwner)
                AppButton("Delete", onClick = onDelete, enabled = isOwner, isDestructive = true)
            }

            Spacer(Modifier.height(20.dp))
            Divider(color = ColorDivider)
            Spacer(Modifier.height(12.dp))

            Text("Calibrations", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(6.dp))

            if (calibrations.isEmpty()) {
                Text("No calibrations yet", style = MaterialTheme.typography.bodySmall)
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 160.dp)) {
                    items(calibrations) { cal ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(ColorSurfaceVar)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    cal.type.name.replace("_", " "),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    cal.result.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (cal.result == CalibrationResult.OK) ColorPrimary else ColorError
                                )
                            }
                            Text(fmt.format(cal.calibratedAt), style = MaterialTheme.typography.labelSmall)
                            if (cal.comment.isNotBlank()) {
                                Text(cal.comment, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = ColorDivider)
            Spacer(Modifier.height(12.dp))

            Text("Maintenance", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(6.dp))

            if (maintenances.isEmpty()) {
                Text("No maintenance records", style = MaterialTheme.typography.bodySmall)
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 160.dp)) {
                    items(maintenances) { m ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(ColorSurfaceVar)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(m.type.name, style = MaterialTheme.typography.bodySmall)
                                Text(fmt.format(m.doneAt), style = MaterialTheme.typography.labelSmall)
                            }
                            Text(m.details, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(80.dp)
        )
        if (value != null) {
            Text(value, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun AppButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isDestructive: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDestructive) ColorError else ColorPrimary,
            contentColor = ColorOnPrimary,
            disabledContainerColor = ColorDivider
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall)
    }
}
