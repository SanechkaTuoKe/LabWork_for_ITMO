package org.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.auth.UserService
import org.example.service.CalibrationService
import org.example.service.InstrumentService
import org.example.service.MaintenanceService
import org.example.ui.auth.AuthView
import org.example.ui.instruments.InstrumentController
import org.example.ui.instruments.InstrumentDetailView
import org.example.ui.instruments.InstrumentMasterView
import org.example.ui.instruments.dialogs.*
import org.example.ui.theme.*

@Composable
fun MainScreen(
    userService: UserService,
    instrumentService: InstrumentService,
    calibrationService: CalibrationService,
    maintenanceService: MaintenanceService
) {
    var isLoggedIn by remember { mutableStateOf(userService.isLoggedIn) }

    if (!isLoggedIn) {
        AuthView(
            userService = userService,
            onAuthSuccess = { isLoggedIn = true }
        )
        return
    }

    val controller = remember {
        InstrumentController(
            instrumentService = instrumentService,
            calibrationService = calibrationService,
            maintenanceService = maintenanceService,
            userService = userService
        )
    }

    var showAdd by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf(false) }
    var showCal by remember { mutableStateOf(false) }
    var showMaint by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showLoadDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(ColorBackground)) {

        // ── Топбар ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorPrimary)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Equipment Manager",
                style = MaterialTheme.typography.titleMedium,
                color = ColorOnPrimary
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопки файловых операций
                TopBarButton("Save", onClick = { showSaveDialog = true })
                TopBarButton("Load", onClick = { showLoadDialog = true })

                Spacer(Modifier.width(8.dp))
                Divider(
                    modifier = Modifier.height(20.dp).width(1.dp),
                    color = ColorOnPrimary.copy(alpha = 0.3f)
                )
                Spacer(Modifier.width(8.dp))

                // Кнопка добавления инструмента
                TopBarButton("+ Add Instrument", onClick = { showAdd = true }, isPrimary = true)

                Spacer(Modifier.width(8.dp))

                // Пользователь и выход
                Text(
                    "● ${userService.currentUsername}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ColorOnPrimary.copy(alpha = 0.8f)
                )
                TextButton(
                    onClick = {
                        userService.logout()
                        isLoggedIn = false
                    },
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("Logout", color = ColorOnPrimary.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Статусная строка
        if (controller.status.value != "Ready") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorPrimaryVar)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    controller.status.value,
                    style = MaterialTheme.typography.labelSmall,
                    color = ColorOnPrimary.copy(alpha = 0.85f)
                )
                TextButton(
                    onClick = { controller.clearStatus() },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text("✕", color = ColorOnPrimary.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Индикатор загрузки
        if (controller.isLoading.value) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = ColorSecondary,
                trackColor = ColorDivider
            )
        }

        // ── Master-Detail ────────────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxSize()) {
            InstrumentMasterView(
                instruments = controller.instruments.value,
                selected = controller.selected.value,
                onSelect = { controller.select(it) }
            )

            Divider(
                modifier = Modifier.fillMaxHeight().width(1.dp),
                color = ColorDivider
            )

            val selectedInstrument = controller.selected.value

            val calibrations = remember(selectedInstrument, controller.calibrationUpdateCounter.value) {
                selectedInstrument?.let { calibrationService.listByInstrument(it.id) } ?: emptyList()
            }

            val maintenances = remember(selectedInstrument, controller.maintenanceUpdateCounter.value) {
                selectedInstrument?.let { maintenanceService.listByInstrument(it.id) } ?: emptyList()
            }

            InstrumentDetailView(
                selected = selectedInstrument,
                calibrations = calibrations,
                maintenances = maintenances,
                currentUsername = userService.currentUsername,
                onEdit = { showEdit = true },
                onDelete = {
                    selectedInstrument?.let { controller.delete(it.id) }
                },
                onCalibrate = { showCal = true },
                onMaintain = { showMaint = true },
                onChangeStatus = {
                    selectedInstrument?.let { inst ->
                        val newStatus = if (inst.status == org.example.domain.InstrumentStatus.ACTIVE)
                            org.example.domain.InstrumentStatus.OUT_OF_SERVICE
                        else
                            org.example.domain.InstrumentStatus.ACTIVE
                        // через сервис напрямую
                        try {
                            instrumentService.changeStatus(inst.id, newStatus)
                            controller.refresh()
                        } catch (e: Exception) {
                            controller.clearError()
                        }
                    }
                }
            )
        }
    }

    // ── Диалоги

    if (controller.error.value != null) {
        AlertDialog(
            onDismissRequest = { controller.clearError() },
            shape = RoundedCornerShape(0.dp),
            title = { Text("Error", style = MaterialTheme.typography.titleSmall, color = ColorError) },
            text = { Text(controller.error.value ?: "", style = MaterialTheme.typography.bodySmall) },
            confirmButton = {
                Button(
                    onClick = { controller.clearError() },
                    shape = RoundedCornerShape(0.dp)
                ) { Text("OK") }
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
                    controller.addCalibration(inst.id, type, result, comment, java.time.Instant.now())
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
                    controller.addMaintenance(inst.id, type, details, java.time.Instant.now())
                    showMaint = false
                },
                onDismiss = { showMaint = false }
            )
        }
    }

    if (showSaveDialog) {
        PathInputDialog(
            title = "Save Data",
            hint = "Directory path, e.g. ./data",
            confirmLabel = "Save",
            onConfirm = { path ->
                controller.saveData(path)
                showSaveDialog = false
            },
            onDismiss = { showSaveDialog = false }
        )
    }

    if (showLoadDialog) {
        PathInputDialog(
            title = "Load Data",
            hint = "Directory path, e.g. ./data",
            confirmLabel = "Load",
            onConfirm = { path ->
                controller.loadData(path)
                showLoadDialog = false
            },
            onDismiss = { showLoadDialog = false }
        )
    }
}

@Composable
private fun TopBarButton(
    text: String,
    onClick: () -> Unit,
    isPrimary: Boolean = false
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) ColorSecondary else ColorPrimaryVar,
            contentColor = ColorOnPrimary
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall)
    }
}
