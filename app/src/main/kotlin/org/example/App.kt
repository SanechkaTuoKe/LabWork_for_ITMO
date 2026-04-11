package org.example

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.dp
import org.example.cli.services.CommandService
import org.example.cli.services.LoopService
import org.example.ui.MainScreen
import org.example.ui.theme.EquipmentAppTheme
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    println("Starting Equipment Manager...")
    val commandService = CommandService()
    val instrumentService = commandService.instrumentService
    val calibrationService = commandService.calibrationService
    val maintenanceService = commandService.maintenanceService

    if (args.isNotEmpty()) {
        try {
            commandService.loadStartupData(args[0])
            println("Loaded data from: ${args[0]}")
        } catch (e: Exception) {
            println("Error loading data: ${e.message}")
        }
    }

    val loopService = LoopService(commandService)

    val cliThread = thread(start = true, isDaemon = true) {
        println("CLI ready. Type 'help' for available commands.")
        loopService.loopOfCommands()
    }

    application {
        Window(
            onCloseRequest = {
                println("UI window closed. Console still running.")
                println("Type 'exit' in console to quit completely.")
                this::exitApplication
            },
            title = "Equipment Manager",
            state = rememberWindowState(width = 1100.dp, height = 700.dp)
        ) {
            EquipmentAppTheme {
                MainScreen(commandService = commandService)
            }
        }
    }
}