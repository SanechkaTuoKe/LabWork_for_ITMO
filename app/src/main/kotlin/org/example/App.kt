package org.example

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.example.auth.UserService
import org.example.service.CalibrationService
import org.example.service.InstrumentService
import org.example.service.MaintenanceService
import org.example.storage.DatabaseConfig
import org.example.ui.MainScreen
import org.example.ui.instruments.InstrumentController
import org.example.ui.theme.EquipmentAppTheme

fun main() {
    val connection = try {
        DatabaseConfig.getConnection()
    } catch (e: Exception) {
        println("FATAL: ${e.message}")
        println("Please check that PostgreSQL is running and try again.")
        return
    }

    val userService = UserService(connection)
    val instrumentService = InstrumentService(connection)
    val calibrationService = CalibrationService(instrumentService, connection)
    val maintenanceService = MaintenanceService(instrumentService, connection)

    val controller = InstrumentController(
        instrumentService,
        calibrationService,
        maintenanceService,
        userService
    )

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Equipment Manager",
            state = rememberWindowState(width = 1200.dp, height = 760.dp)
        ) {
            EquipmentAppTheme {
                MainScreen(
                    userService,
                    controller,
                    calibrationService,
                    maintenanceService
                )
            }
        }
    }
}