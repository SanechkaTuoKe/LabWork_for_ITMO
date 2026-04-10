package org.example

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.example.cli.services.CommandService
import org.example.ui.theme.AppTheme
import androidx.compose.ui.unit.dp
import org.example.ui.MainView

fun main(args: Array<String>) {
    println("Starting Equipment Manager...")

    val commandService = CommandService()

    if (args.isNotEmpty()) {
        try {
            commandService.loadStartupData(args[0])
            println("Loaded from: ${args[0]}")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Equipment Manager",
            state = rememberWindowState(width = 1100.dp, height = 700.dp)
        ) {
            MainView(commandService = commandService)
        }
    }
}