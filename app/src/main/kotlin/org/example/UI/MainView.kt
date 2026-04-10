package org.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import org.example.cli.services.CommandService
import org.example.service.CalibrationService
import org.example.service.MaintenanceService
import org.example.ui.instruments.InstrumentController
import org.example.ui.instruments.InstrumentDetailView
import org.example.ui.instruments.InstrumentMasterView
import org.example.ui.instruments.dialogs.*
import org.example.ui.theme.AppTheme

@Composable
fun MainView(commandService: CommandService) {
    val controller = remember {
        val instService = commandService.instrumentService
        InstrumentController(
            instrumentService = instService,
            calibrationService = CalibrationService(instService),
            maintenanceService = MaintenanceService(instService)
        )
    }

    var showAdd by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf(false) }
    var showCal by remember { mutableStateOf(false) }
    var showMaint by remember { mutableStateOf(false) }

    AppTheme {
        Box {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { controller.refresh() },
                        enabled = !controller.isLoading.value
                    ) {
                        Text("Refresh")
                    }
                    Button(onClick = { showAdd = true }) {
                        Text("Add")
                    }
                    Button(
                        onClick = { showEdit = true },
                        enabled = controller.selected.value != null
                    ) {
                        Text("Edit")
                    }
                    Text(controller.status.value)
                }

                if (controller.isLoading.value) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                Row {
                    InstrumentMasterView(
                        instruments = controller.instruments.value,
                        selected = controller.selected.value,
                        onSelect = { controller.select(it) }
                    )

                    val selectedInstrument = controller.selected.value
                    val calibrations = remember(selectedInstrument) {
                        selectedInstrument?.let { controller.calibrationService.listByInstrument(it.id) } ?: emptyList()
                    }
                    val maintenances = remember(selectedInstrument) {
                        selectedInstrument?.let { controller.maintenanceService.listByInstrument(it.id) } ?: emptyList()
                    }

                    InstrumentDetailView(
                        selected = selectedInstrument,
                        calibrations = calibrations,
                        maintenances = maintenances,
                        onEdit = { showEdit = true },
                        onDelete = {
                            selectedInstrument?.let { inst ->
                                controller.delete(inst.id)
                            }
                        },
                        onCalibrate = { showCal = true },
                        onMaintain = { showMaint = true }
                    )
                }
            }

            if (controller.error.value != null) {
                AlertDialog(
                    onDismissRequest = { controller.clearError() },
                    title = { Text("Error") },
                    text = { Text(controller.error.value ?: "") },
                    confirmButton = {
                        TextButton(onClick = { controller.clearError() }) {
                            Text("OK")
                        }
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

            if (showEdit && controller.selected.value != null) {
                EditInstrumentDialog(
                    instrument = controller.selected.value!!,
                    onConfirm = { name, type, loc, inv ->
                        controller.edit(controller.selected.value!!.id, name, type, loc, inv)
                        showEdit = false
                    },
                    onDismiss = { showEdit = false }
                )
            }

            if (showCal && controller.selected.value != null) {
                CalibrationDialog(
                    onConfirm = { type, result, comment ->
                        controller.addCalibration(
                            controller.selected.value!!.id,
                            type,
                            result,
                            comment,
                            java.time.Instant.now()
                        )
                        showCal = false
                    },
                    onDismiss = { showCal = false }
                )
            }

            if (showMaint && controller.selected.value != null) {
                MaintenanceDialog(
                    onConfirm = { type, details ->
                        controller.addMaintenance(
                            controller.selected.value!!.id,
                            type,
                            details,
                            java.time.Instant.now()
                        )
                        showMaint = false
                    },
                    onDismiss = { showMaint = false }
                )
            }
        }
    }
}