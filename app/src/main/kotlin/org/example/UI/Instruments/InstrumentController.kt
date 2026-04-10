package org.example.ui.instruments

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.*
import org.example.domain.*
import org.example.service.*
import java.time.Instant

class InstrumentController(
    val instrumentService: InstrumentService,
    val calibrationService: CalibrationService,
    val maintenanceService: MaintenanceService
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var instruments = mutableStateOf(listOf<Instrument>())
    var selected = mutableStateOf<Instrument?>(null)
    var isLoading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)
    var status = mutableStateOf("Ready")

    fun refresh() {
        scope.launch {
            isLoading.value = true
            try {
                val list = instrumentService.getAll()
                instruments.value = list
                status.value = "Loaded: ${list.size}"
            } catch (e: Exception) {
                error.value = e.message
            }
            isLoading.value = false
        }
    }

    fun select(inst: Instrument) {
        selected.value = inst
    }

    fun add(name: String, type: InstrumentType, location: String, inventory: String?) {
        scope.launch {
            try {
                instrumentService.add(name, type, inventory, location)
                refresh()
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun edit(id: Long, name: String, type: InstrumentType, location: String, inventory: String?) {
        scope.launch {
            try {
                instrumentService.update(id, name, location, inventory)
                refresh()
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun delete(id: Long) {
        scope.launch {
            try {
                instrumentService.delete(id)
                selected.value = null
                refresh()
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun addCalibration(
        id: Long,
        type: String,
        result: String,
        comment: String?,
        date: Instant
    ) {
        scope.launch {
            try {
                calibrationService.add(
                    instrumentId = id,
                    typeInput = type,
                    resultInput = result,
                    comment = comment,
                    ownerUsername = "SYSTEM",
                    calibratedAt = date
                )
                refresh()
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun addMaintenance(
        id: Long,
        type: MaintenanceType,
        details: String,
        date: Instant
    ) {
        scope.launch {
            try {
                maintenanceService.add(
                    instrumentId = id,
                    type = type,
                    details = details,
                    ownerUsername = "SYSTEM",
                    doneAt = date
                )
                refresh()
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun clearError() {
        error.value = null
    }
}