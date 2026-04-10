package org.example.ui
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import org.example.cli.services.CommandService
import org.example.service.CalibrationService
import org.example.service.MaintenanceService

import org.example.ui.components.ErrorDialog
import org.example.ui.components.LoadingIndicator

import org.example.ui.instruments.InstrumentController
import org.example.ui.instruments.InstrumentDetailView
import org.example.ui.instruments.InstrumentMasterView

import org.example.ui.instruments.dialogs.AddInstrumentDialog
import org.example.ui.instruments.dialogs.CalibrationDialog
import org.example.ui.instruments.dialogs.EditInstrumentDialog
import org.example.ui.instruments.dialogs.MaintenanceDialog

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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
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
                    LoadingIndicator()
                }

                Row(modifier = Modifier.fillMaxSize().weight(1f)) {
                    InstrumentMasterView(
                        instruments = controller.instruments.value,
                        selected = controller.selected.value,
                        onSelect = { controller.select(it) }
                    )

                    val selectedInstrument = controller.selected.value

                    val calibrations = remember(
                        selectedInstrument,
                        controller.calibrationUpdateCounter.value
                    ) {
                        val inst = selectedInstrument
                        if (inst != null) {
                            controller.calibrationService.listByInstrument(inst.id)
                        } else {
                            emptyList()
                        }
                    }

                    val maintenances = remember(
                        selectedInstrument,
                        controller.maintenanceUpdateCounter.value
                    ) {
                        val inst = selectedInstrument
                        if (inst != null) {
                            controller.maintenanceService.listByInstrument(inst.id)
                        } else {
                            emptyList()
                        }
                    }

                    InstrumentDetailView(
                        selected = selectedInstrument,
                        calibrations = calibrations,
                        maintenances = maintenances,
                        onEdit = { showEdit = true },
                        onDelete = {
                            val inst = controller.selected.value
                            if (inst != null) {
                                controller.delete(inst.id)
                            }
                        },
                        onCalibrate = { showCal = true },
                        onMaintain = { showMaint = true }
                    )
                }
            }

            if (controller.error.value != null) {
                ErrorDialog(
                    message = controller.error.value ?: "",
                    onDismiss = { controller.clearError() }
                )
            }

            if (showAdd) {
                AddInstrumentDialog(
                    onConfirm = { name, type, loc, inv ->
                        controller.add(name, type, loc, if (inv.isNullOrBlank()) null else inv)
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
                            controller.edit(
                                inst.id,
                                name,
                                loc,
                                if (inv.isNullOrBlank()) null else inv
                            )
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
                            controller.addCalibration(
                                inst.id,
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
            }

            if (showMaint) {
                val inst = controller.selected.value
                if (inst != null) {
                    MaintenanceDialog(
                        onConfirm = { type, details ->
                            controller.addMaintenance(
                                inst.id,
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
}