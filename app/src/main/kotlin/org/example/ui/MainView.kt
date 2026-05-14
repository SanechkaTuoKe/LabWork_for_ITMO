package org.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.auth.UserService
import org.example.domain.Calibration
import org.example.domain.InstrumentStatus
import org.example.domain.Maintenance
import org.example.service.CalibrationService
import org.example.service.MaintenanceService
import org.example.ui.auth.AuthView
import org.example.ui.instruments.InstrumentController
import org.example.ui.instruments.InstrumentDetailView
import org.example.ui.instruments.InstrumentMasterView
import org.example.ui.instruments.dialogs.AddInstrumentDialog
import org.example.ui.instruments.dialogs.CalibrationDialog
import org.example.ui.instruments.dialogs.EditInstrumentDialog
import org.example.ui.instruments.dialogs.MaintenanceDialog
import java.time.Instant

@Composable
fun MainScreen(
    userService: UserService,
    controller: InstrumentController,
    calibrationService: CalibrationService,
    maintenanceService: MaintenanceService
) {
    var isLoggedIn by remember { mutableStateOf(userService.isLoggedIn) }

    if (!isLoggedIn) {
        AuthView(
            userService = userService,
            onAuthSuccess = {
                isLoggedIn = true
                controller.refresh()
            }
        )
        return
    }

    var showAdd by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf(false) }
    var showCal by remember { mutableStateOf(false) }
    var showMaint by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // топбар
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Equipment Manager",
                color = MaterialTheme.colorScheme.onPrimary
            )

            Button(onClick = { controller.refresh() }) { Text("Refresh") }
            Button(onClick = { showAdd = true }) { Text("Add") }

            Spacer(Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                userService.currentUsername ?: "",
                color = MaterialTheme.colorScheme.onPrimary
            )

            TextButton(onClick = {
                userService.logout()
                isLoggedIn = false
            }) {
                Text("Logout", color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        // статусная
        if (controller.status.value != "Ready") {
            Text(
                controller.status.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        if (controller.isLoading.value) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        Row(Modifier.fillMaxSize()) {
            InstrumentMasterView(
                instruments = controller.instruments.value,
                selected = controller.selected.value,
                onSelect = { controller.select(it) }
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
            )

            val selected = controller.selected.value

            var calList: List<Calibration> = emptyList()
            var maintList: List<Maintenance> = emptyList()

            if (selected != null) {
                calList = remember(selected.id, controller.calibrationUpdateCounter.value) {
                    calibrationService.listByInstrument(selected.id)
                }
                maintList = remember(selected.id, controller.maintenanceUpdateCounter.value) {
                    maintenanceService.listByInstrument(selected.id)
                }
            }

            InstrumentDetailView(
                selected = selected,
                calibrations = calList,
                maintenances = maintList,
                currentUsername = userService.currentUsername,
                onEdit = { showEdit = true },
                onDelete = {
                    if (selected != null) {
                        controller.delete(selected.id)
                    }
                },
                onCalibrate = { showCal = true },
                onMaintain = { showMaint = true },
                onChangeStatus = {
                    if (selected != null) {
                        val newStatus = if (selected.status == InstrumentStatus.ACTIVE) {
                            InstrumentStatus.OUT_OF_SERVICE
                        } else {
                            InstrumentStatus.ACTIVE
                        }
                        controller.changeStatus(selected.id, newStatus)
                    }
                }
            )
        }
    }

    val err = controller.error.value
    if (err != null) {
        AlertDialog(
            onDismissRequest = { controller.clearError() },
            title = { Text("Error") },
            text = { Text(err) },
            confirmButton = {
                Button(onClick = { controller.clearError() }) { Text("OK") }
            }
        )
    }

    if (showAdd) {
        AddInstrumentDialog(
            onConfirm = { name, type, loc, inv ->
                controller.add(name, type, loc, inv)
                showAdd = false
            },
            onDismiss = { showAdd = false }
        )
    }

    if (showEdit) {
        val inst = controller.selected.value
        if (inst != null) {
            EditInstrumentDialog(
                instrument = inst,
                onConfirm = { name, type, loc, inv ->
                    controller.edit(inst.id, name, loc, inv)
                    showEdit = false
                },
                onDismiss = { showEdit = false }
            )
        }
    }

    if (showCal) {
        val inst = controller.selected.value
        if (inst != null) {
            CalibrationDialog(
                onConfirm = { type, result, comment ->
                    controller.addCalibration(inst.id, type, result, comment, Instant.now())
                    showCal = false
                },
                onDismiss = { showCal = false }
            )
        }
    }

    if (showMaint) {
        val inst = controller.selected.value
        if (inst != null) {
            MaintenanceDialog(
                onConfirm = { type, details ->
                    controller.addMaintenance(inst.id, type, details, Instant.now())
                    showMaint = false
                },
                onDismiss = { showMaint = false }
            )
        }
    }
}