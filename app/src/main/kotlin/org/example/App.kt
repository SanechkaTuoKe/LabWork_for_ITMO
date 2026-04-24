package org.example

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.example.auth.UserService
import org.example.service.CalibrationService
import org.example.service.InstrumentService
import org.example.service.MaintenanceService
import org.example.storage.StorageService
import org.example.ui.MainScreen
import org.example.ui.instruments.InstrumentController
import org.example.ui.theme.EquipmentAppTheme

fun main() {
    val userService = UserService()

    val instrumentService = InstrumentService()
    val calibrationService = CalibrationService(instrumentService)
    val maintenanceService = MaintenanceService(instrumentService)

    val storageService = StorageService(
        instrumentService,
        calibrationService,
        maintenanceService
    )

    val controller = InstrumentController(
        instrumentService,
        calibrationService,
        maintenanceService,
        storageService,
        userService
    )

    // val loopService = LoopService(commandService)
// loopService.loopOfCommands()
    
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
