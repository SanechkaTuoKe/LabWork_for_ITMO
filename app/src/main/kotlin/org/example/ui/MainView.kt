package org.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import org.example.ui.instruments.dialogs.FilePickerDialog
import org.example.ui.instruments.dialogs.MaintenanceDialog
import org.example.ui.theme.ColorBackground
import org.example.ui.theme.ColorOnPrimary
import org.example.ui.theme.ColorPrimary
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
    var showSave by remember { mutableStateOf(false) }
    var showLoad by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorPrimary)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Equipment Manager", color = ColorOnPrimary)

            Button(onClick = { controller.refresh() }) { Text("Refresh") }
            Button(onClick = { showAdd = true }) { Text("Add") }
            Button(onClick = { showSave = true }) { Text("Save") }
            Button(onClick = { showLoad = true }) { Text("Load") }

            Spacer(Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = ColorOnPrimary
            )
            Text(userService.currentUsername ?: "", color = ColorOnPrimary)

            TextButton(onClick = {
                userService.logout()
                isLoggedIn = false
            }) {
                Text("Logout", color = ColorOnPrimary)
            }
        }

        if (controller.status.value != "Ready") {
            Text(
                controller.status.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorPrimary.copy(alpha = 0.8f))
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                color = ColorOnPrimary
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

    if (showSave) {
        FilePickerDialog(
            title = "Save",
            hint = "./data",
            confirmLabel = "Save",
            onConfirm = { path ->
                controller.save(path)
                showSave = false
            },
            onDismiss = { showSave = false }
        )
    }

    if (showLoad) {
        FilePickerDialog(
            title = "Load",
            hint = "./data",
            confirmLabel = "Load",
            onConfirm = { path ->
                controller.load(path)
                showLoad = false
            },
            onDismiss = { showLoad = false }
        )
    }
}
