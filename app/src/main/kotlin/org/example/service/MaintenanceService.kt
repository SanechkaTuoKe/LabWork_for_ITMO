package org.example.service

import org.example.storage.saveLoad.InstrumentSaveLoad
import java.sql.Connection
import org.example.domain.Maintenance
import org.example.domain.MaintenanceType
import org.example.validation.MaintenanceValidator
import java.time.Instant
import java.util.*
import org.example.storage.saveLoad.MaintenanceSaveLoad

class MaintenanceService(
    private val instrumentService: InstrumentService,
    private val connection: Connection? = null  // ← это нужно добавить
) {
    private val maintenances = TreeMap<Long, Maintenance>()
    private var nextId = 1L
    private val dbStorage = connection?.let { MaintenanceSaveLoad.create(it) }
    init {
        connection?.let {
            loadFromDatabase()
        }
    }

    fun loadFromDatabase() {
        dbStorage?.let { storage ->
            val loaded = storage.load()
            maintenances.clear()
            maintenances.putAll(loaded)
            nextId = if (loaded.isEmpty()) 1L else loaded.keys.max() + 1L
        }
    }

    fun add(
        instrumentId: Long,
        type: MaintenanceType,
        details: String,
        ownerUsername: String,
        doneAt: Instant = Instant.now()
    ): Maintenance {
        val instrument = instrumentService.getById(instrumentId)
            ?: throw IllegalArgumentException("Instrument with id=$instrumentId not found")
        val validatedDetails = MaintenanceValidator.validateDetails(details)

        if (dbStorage != null) {
            val tempMaintenance = Maintenance(
                id = 0,
                instrumentId = instrument.id,
                type = type,
                details = validatedDetails,
                doneAt = doneAt,
                ownerUsername = ownerUsername,
                createdAt = Instant.now()
            )
            val generatedId = dbStorage!!.insert(tempMaintenance)
            if (generatedId != null) {
                val maintenance = Maintenance(
                    id = generatedId,
                    instrumentId = tempMaintenance.instrumentId,
                    type = tempMaintenance.type,
                    details = tempMaintenance.details,
                    doneAt = tempMaintenance.doneAt,
                    ownerUsername = tempMaintenance.ownerUsername,
                    createdAt = tempMaintenance.createdAt
                )
                maintenances[maintenance.id] = maintenance
                return maintenance
            }
        }
        val maintenance = Maintenance(
            id = nextId++,
            instrumentId = instrument.id,
            type = type,
            details = validatedDetails,
            doneAt = doneAt,
            ownerUsername = ownerUsername,
            createdAt = Instant.now()
        )
        maintenances[maintenance.id] = maintenance
        return maintenance
    }

    fun getById(id: Long): Maintenance =
        maintenances[id] ?: throw NoSuchElementException("Maintenance with id=$id not found")

    fun getAll(): List<Maintenance> = maintenances.values.toList()

    fun listByInstrument(instrumentId: Long, last: Int? = null): List<Maintenance> {
        require(instrumentService.getById(instrumentId) != null) {
            "Instrument with id=$instrumentId not found"
        }
        val list = maintenances.values
            .filter { it.instrumentId == instrumentId }
            .sortedByDescending { it.doneAt }
        return if (last != null && last > 0) list.take(last) else list
    }

    fun removeByInstrumentId(instrumentId: Long) {
        dbStorage?.let { storage ->
            maintenances.values.filter { it.instrumentId == instrumentId }.forEach {
                storage.delete(it.id)
            }
        }
        maintenances.entries.removeIf { it.value.instrumentId == instrumentId }
    }

    fun loadAll(loaded: Map<Long, Maintenance>) {
        maintenances.clear()
        maintenances.putAll(loaded)
        nextId = if (loaded.isEmpty()) 1L else loaded.keys.max() + 1L
    }

    fun addExisting(maintenance: Maintenance) {
        maintenances[maintenance.id] = maintenance
        if (maintenance.id >= nextId) {
            nextId = maintenance.id + 1
        }
    }

    fun clearAll() {
        maintenances.clear()
        nextId = 1L
    }

    internal fun getAllMaintenances(): TreeMap<Long, Maintenance> = maintenances
}
