package org.example.service

import org.example.domain.Maintenance
import org.example.domain.MaintenanceType
import org.example.validation.MaintenanceValidator
import java.time.Instant
import java.util.*

class MaintenanceService(
    private val instrumentService: InstrumentService
) {

    private val maintenances = TreeMap<Long, Maintenance>()
    private var nextId = 1L

    fun add(
        instrumentId: Long,
        type: MaintenanceType,
        details: String,
        ownerUsername: String = "SYSTEM",
        doneAt: Instant = Instant.now()
    ): Maintenance {

        val instrument = instrumentService.getById(instrumentId)
            ?: throw IllegalArgumentException("Instrument with id=$instrumentId not found")

        val validatedDetails = MaintenanceValidator.validateDetails(details)

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
        maintenances[id]
            ?: throw NoSuchElementException("Maintenance with id=$id not found")

    fun listByInstrument(instrumentId: Long, last: Int? = null): List<Maintenance> {

        require(instrumentService.getById(instrumentId) != null) {
            "Instrument with id=$instrumentId not found"
        }

        val list: List<Maintenance> = maintenances.values
            .filter { it.instrumentId == instrumentId }
            .sortedByDescending { it.doneAt }

        return if (last != null && last > 0) list.take(last) else list
    }

    fun removeByInstrumentId(instrumentId: Long) {
        maintenances.entries.removeIf { it.value.instrumentId == instrumentId }
    }
}