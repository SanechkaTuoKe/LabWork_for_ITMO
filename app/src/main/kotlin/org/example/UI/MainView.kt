package org.example.ui

import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import org.example.service.InstrumentService
import org.example.ui.instruments.*

@Composable
fun WindowScope.MainView() {

    val service = remember { InstrumentService() }
    val controller = remember { InstrumentController(service) }

    DisposableEffect(Unit) {
        onDispose {
            // ничего не нужно
        }
    }

    InstrumentView(controller)
}