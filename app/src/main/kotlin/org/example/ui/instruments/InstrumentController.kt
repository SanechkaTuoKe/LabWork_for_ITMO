package org.example.ui.instruments

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.*
import org.example.auth.UserService
import org.example.domain.*
import org.example.service.*
import java.time.Instant

class InstrumentController(
    val instrumentService: InstrumentService,
    val calibrationService: CalibrationService,
    val maintenanceService: MaintenanceService,
    val userService: UserService
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var autoRefreshJob: Job? = null

    private var filterMode = "mine"
    private var filterOwner: String? = null

    val instruments = mutableStateOf<List<Instrument>>(emptyList())
    val selected = mutableStateOf<Instrument?>(null)
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    val status = mutableStateOf("Ready")
    val calibrationUpdateCounter = mutableStateOf(0)
    val maintenanceUpdateCounter = mutableStateOf(0)

    init {
        refresh()
        startAutoRefresh()
    }

    fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = scope.launch {
            while (isActive) {
                delay(3000L)
                refreshSilent()
            }
        }
    }

    fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
    }

    private suspend fun refreshSilent() {
        try {
            instrumentService.loadFromDatabase()
            calibrationService.loadFromDatabase()
            maintenanceService.loadFromDatabase()

            val all = instrumentService.getAll()
            val newList = when (filterMode) {
                "mine" -> {
                    val username = userService.currentUsername
                    all.filter { it.ownerUsername == username }
                }
                "all" -> all
                else -> all.filter { it.ownerUsername == filterOwner }
            }

            val selectedId = selected.value?.id
            val stillExists = newList.any { it.id == selectedId }

            instruments.value = newList
            if (!stillExists) selected.value = null

        } catch (_: Exception) { }
    }

    fun refresh() {
        filterMode = "mine"
        filterOwner = null
        scope.launch {
            isLoading.value = true
            try {
                instrumentService.loadFromDatabase()
                calibrationService.loadFromDatabase()
                maintenanceService.loadFromDatabase()

                val username = userService.currentUsername
                val all = instrumentService.getAll()

                instruments.value = if (username != null) {
                    all.filter { it.ownerUsername == username }
                } else {
                    emptyList()
                }

                if (selected.value != null) {
                    val stillExists = instruments.value.any { it.id == selected.value!!.id }
                    if (!stillExists) {
                        selected.value = null
                    }
                }

                status.value = "Loaded ${instruments.value.size} instruments"
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
        val username = requireAuth() ?: return
        scope.launch {
            try {
                instrumentService.add(name, type, inventory, location, username)
                refresh()
                status.value = "Added"
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun edit(id: Long, name: String, location: String, inventory: String?) {
        val username = requireAuth() ?: return
        scope.launch {
            try {
                val inst = instrumentService.getById(id)
                if (inst != null && inst.ownerUsername != username) {
                    error.value = "You don't have permission to edit this instrument"
                    return@launch
                }
                instrumentService.update(id, name, location, inventory)
                selected.value = instrumentService.getById(id)
                refresh()
                status.value = "Updated"
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun delete(id: Long) {
        val username = requireAuth() ?: return
        scope.launch {
            try {
                val inst = instrumentService.getById(id)
                if (inst != null && inst.ownerUsername != username) {
                    error.value = "You don't have permission to delete this instrument"
                    return@launch
                }
                instrumentService.delete(id)
                if (selected.value?.id == id) {
                    selected.value = null
                }
                refresh()
                status.value = "Deleted"
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun changeStatus(id: Long, newStatus: InstrumentStatus) {
        val username = requireAuth() ?: return
        scope.launch {
            try {
                val inst = instrumentService.getById(id)
                if (inst != null && inst.ownerUsername != username) {
                    error.value = "No permission"
                    return@launch
                }
                instrumentService.changeStatus(id, newStatus)
                refresh()
                status.value = "Status changed"
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun addCalibration(
        instrumentId: Long, type: String, result: String,
        comment: String?, date: Instant
    ) {
        val username = requireAuth() ?: return
        scope.launch {
            try {
                calibrationService.add(instrumentId, type, result, comment, username, date)
                calibrationUpdateCounter.value++
                status.value = "Calibration added"
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun addMaintenance(
        instrumentId: Long, type: MaintenanceType,
        details: String, date: Instant
    ) {
        val username = requireAuth() ?: return
        scope.launch {
            try {
                maintenanceService.add(instrumentId, type, details, username, date)
                maintenanceUpdateCounter.value++
                status.value = "Maintenance added"
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun clearError() { error.value = null }
    fun clearStatus() { status.value = "Ready" }

    private fun requireAuth(): String? {
        val username = userService.currentUsername
        if (username == null) {
            error.value = "You must be logged in"
            return null
        }
        return username
    }

    fun loadByOwner(owner: String) {
        filterMode = "owner"
        filterOwner = owner
        scope.launch {
            isLoading.value = true
            try {
                instrumentService.loadFromDatabase()
                val all = instrumentService.getAll()
                val loadedByOwner = all.filter { it.ownerUsername == owner }
                val currentIds = instruments.value.map { it.id }.toSet()
                val newOnes = loadedByOwner.filter { it.id !in currentIds }
                instruments.value = instruments.value + newOnes
                status.value = "Added ${newOnes.size} instruments from $owner"
            } catch (e: Exception) {
                error.value = e.message
            }
            isLoading.value = false
        }
    }
}