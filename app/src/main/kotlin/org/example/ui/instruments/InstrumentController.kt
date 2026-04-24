package org.example.ui.instruments

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.*
import org.example.auth.UserService
import org.example.domain.*
import org.example.service.*
import org.example.storage.StorageService
import java.time.Instant

class InstrumentController(
    val instrumentService: InstrumentService,
    val calibrationService: CalibrationService,
    val maintenanceService: MaintenanceService,
    val userService: UserService
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var instruments = mutableStateOf<List<Instrument>>(emptyList())
    var selected = mutableStateOf<Instrument?>(null)
    var isLoading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)
    var status = mutableStateOf("Ready")
    var calibrationUpdateCounter = mutableStateOf(0)
    var maintenanceUpdateCounter = mutableStateOf(0)

    private fun getUserDataDir(): java.nio.file.Path {
        val username = userService.currentUsername
        return Paths.get("./data/$username")
    }

    init {
        if (userService.isLoggedIn) {
            loadUserData()
        }
        refresh()
    }

    fun onUserLoggedIn() {
        loadUserData()
        refresh()
    }

    private fun loadUserData() {
        scope.launch {
            try {
                val userDir = getUserDataDir()
                val instrumentSL = InstrumentSaveLoad.create(userDir.resolve("instruments.csv"))
                val calibrationSL = CalibrationSaveLoad.create(userDir.resolve("calibrations.csv"))
                val maintenanceSL = MaintenanceSaveLoad.create(userDir.resolve("maintenances.csv"))

                if (instrumentSL.exists()) {
                    val loadedInstruments = instrumentSL.load()
                    instrumentService.clearAll()
                    loadedInstruments.values.forEach { instrument ->
                        instrumentService.addExisting(instrument)
                    }
                }

                if (calibrationSL.exists()) {
                    calibrationService.clearAll()
                    calibrationSL.load().values.forEach { calibration ->
                        calibrationService.addExisting(calibration)
                    }
                }

                if (maintenanceSL.exists()) {
                    maintenanceService.clearAll()
                    maintenanceSL.load().values.forEach { maintenance ->
                        maintenanceService.addExisting(maintenance)
                    }
                }
                status.value = "Data loaded"
            } catch (e: Exception) {
            }
        }
    }
    private fun saveUserData() {
        scope.launch {
            try {
                val userDir = getUserDataDir()
                java.nio.file.Files.createDirectories(userDir)

                InstrumentSaveLoad.create(userDir.resolve("instruments.csv"))
                    .save(instrumentService.getAll())

                CalibrationSaveLoad.create(userDir.resolve("calibrations.csv"))
                    .save(calibrationService.getAll())

                MaintenanceSaveLoad.create(userDir.resolve("maintenances.csv"))
                    .save(maintenanceService.getAll())
            } catch (e: Exception) {
                error.value = "Auto-save error: ${e.message}"
            }
        }
    }

    fun refresh() {
        scope.launch {
            isLoading.value = true
            instruments.value = instrumentService.getAll()
            calibrationUpdateCounter.value++
            maintenanceUpdateCounter.value++
            isLoading.value = false
        }
    }

    fun select(inst: Instrument) {
        selected.value = inst
    }

    private fun requireAuth(): Boolean {
        if (!userService.isLoggedIn) {
            error.value = "You must be logged in to perform this action"
            return false
        }
        return true
    }

    fun add(name: String, type: InstrumentType, location: String, inventory: String?) {
        if (!requireAuth()) return
        scope.launch {
            try {
                instrumentService.add(name, type, inventory, location, userService.currentUsername)
                refresh(); status.value = "Instrument added"
            } catch (e: Exception) { error.value = e.message }
        }
    }

    fun edit(id: Long, name: String, location: String, inventory: String?) {
        if (!requireAuth()) return
        scope.launch {
            try {
                val inst = instrumentService.getById(id)
                if (inst != null && inst.ownerUsername != userService.currentUsername) {
                    error.value = "Error: you don't have permission to edit this instrument"
                    return@launch
                }
                instrumentService.update(id, name, location, inventory)
                val updated = instrumentService.getById(id)
                if (selected.value?.id == id && updated != null) selected.value = updated
                refresh(); status.value = "Instrument updated"
            } catch (e: Exception) { error.value = e.message }
        }
    }

    fun delete(id: Long) {
        if (!requireAuth()) return
        scope.launch {
            try {
                val inst = instrumentService.getById(id)
                if (inst != null && inst.ownerUsername != userService.currentUsername) {
                    error.value = "Error: you don't have permission to delete this instrument"
                    return@launch
                }
                instrumentService.delete(id)
                if (selected.value?.id == id) selected.value = null
                refresh(); status.value = "Instrument deleted"
            } catch (e: Exception) { error.value = e.message }
        }
    }

    fun addCalibration(id: Long, type: String, result: String, comment: String?, date: Instant) {
        if (!requireAuth()) return
        scope.launch {
            try {
                calibrationService.add(id, type, result, comment, userService.currentUsername, date)
                calibrationUpdateCounter.value++
                status.value = "Calibration added"
            } catch (e: Exception) { error.value = e.message }
        }
    }

    fun addMaintenance(id: Long, type: MaintenanceType, details: String, date: Instant) {
        if (!requireAuth()) return
        scope.launch {
            try {
                maintenanceService.add(id, type, details, userService.currentUsername, date)
                maintenanceUpdateCounter.value++
                status.value = "Maintenance added"
            } catch (e: Exception) { error.value = e.message }
        }
    }

    fun saveData(directory: String) {
        scope.launch {
            try { storageService.save(directory); status.value = "Saved to $directory" }
            catch (e: Exception) { error.value = "Save error: ${e.message}" }
        }
    }

    fun loadData(directory: String) {
        scope.launch {
            try { storageService.load(directory); refresh(); status.value = "Loaded from $directory" }
            catch (e: Exception) { error.value = "Load error: ${e.message}" }
        }
    }

    fun clearError() { error.value = null }
    fun clearStatus() { status.value = "Ready" }
}
