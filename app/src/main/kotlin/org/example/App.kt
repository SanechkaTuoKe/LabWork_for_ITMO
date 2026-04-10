package org.example

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.service.InstrumentService
import org.example.ui.instruments.InstrumentController
import org.example.ui.instruments.InstrumentView
import org.example.ui.theme.AppTheme

fun main() {

    application {

        val service = InstrumentService()
        val controller = InstrumentController(service)

        Window(
            onCloseRequest = ::exitApplication,
            title = "Instruments"
        ) {

            AppTheme {
                InstrumentView(controller)
            }
        }
    }
}