package org.example

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.example.auth.UserService
import org.example.service.CalibrationService
import org.example.service.InstrumentService
import org.example.service.MaintenanceService
import org.example.ui.MainScreen
import org.example.ui.theme.EquipmentAppTheme

fun main() {
    val userService = UserService()
    val instrumentService = InstrumentService()
    val calibrationService = CalibrationService(instrumentService)
    val maintenanceService = MaintenanceService(instrumentService)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Equipment Manager",
            state = rememberWindowState(width = 1200.dp, height = 760.dp)
        ) {
            EquipmentAppTheme {
                MainScreen(
                    userService = userService,
                    instrumentService = instrumentService,
                    calibrationService = calibrationService,
                    maintenanceService = maintenanceService
                )
            }
        }
    }
}
