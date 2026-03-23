package org.example.service

import org.example.domain.Maintenance
import org.example.domain.MaintenanceType
import org.example.validation.MaintenanceValidator
import java.time.Instant
import java.util.*

class MaintenanceService(private val instrumentService: InstrumentService) {
    private val maintenances = TreeMap<Long, Maintenance>()
    private var nextId = 1L

    fun add(
        instrumentId: Long,
        type: MaintenanceType,
        details: String,
        ownerUsername: String = "SYSTEM",
        doneAt: Instant = Instant.now()
    ): Maintenance {
        if (instrumentService.getById(instrumentId) == null) {
            throw IllegalArgumentException("Ошибка: прибор с id=$instrumentId не найден")
        }

        MaintenanceValidator.validateDetails(details)

        val maintenance = Maintenance(
            id = nextId++,
            instrumentId = instrumentId,
            type = type,
            details = details,
            doneAt = doneAt,
            ownerUsername = ownerUsername,
            createdAt = Instant.now()
        )

        maintenances[maintenance.id] = maintenance
        return maintenance
    }

    fun getById(id: Long): Maintenance =
        maintenances[id] ?: throw NoSuchElementException("Ошибка: обслуживание с id=$id не найдено")

    fun listByInstrument(instrumentId: Long, last: Int? = null): List<Maintenance> {
        if (instrumentService.getById(instrumentId) == null) {
            throw IllegalArgumentException("Ошибка: прибор с id=$instrumentId не найден")
        }

        val result = maintenances.values
            .filter { it.instrumentId == instrumentId }
            .sortedByDescending { it.doneAt }

        return if (last != null && last > 0) {
            result.take(last)
        } else {
            result
        }
    }

    fun getByInstrumentID(instrumentID: Long) =
        maintenances.filterValues {
            it.instrumentId == instrumentID
        }

    fun removeById(instrumentId: Long) {
        getByInstrumentID(instrumentId)
            .forEach {
                maintenances.remove(it.key)
            }

    }
}