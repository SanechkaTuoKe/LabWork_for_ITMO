package org.example.service

import org.example.domain.*
import org.example.validation.InstrumentValidator
import java.time.Instant

class InstrumentService {
    private val instruments = mutableMapOf<Long, Instrument>()
    private var nextId = 1L
    private val calibrationService = CalibrationService(this)
    private val maintenanceService = MaintenanceService(this)
    fun add(
        name: String,
        type: InstrumentType,
        inventoryNumber: String?,
        location: String,
        ownerUsername: String = "SYSTEM"
    ): Instrument {
        InstrumentValidator.validateName(name)
        InstrumentValidator.validateLocation(location)
        InstrumentValidator.validateInventoryNumber(inventoryNumber)

        val now = Instant.now()
        val instrument = Instrument(
            id = nextId++,
            name = name,
            type = type,
            inventoryNumber = inventoryNumber,
            location = location,
            status = InstrumentStatus.ACTIVE,
            ownerUsername = ownerUsername,
            createdAt = now,
            updatedAt = now
        )
        instruments[instrument.id] = instrument
        return instrument
    }

    fun getById(id: Long): Instrument? = instruments[id]
    fun getAll(): List<Instrument> = instruments.values.toList()

    fun list(type: InstrumentType?, status: InstrumentStatus?): List<Instrument> =
        instruments.values.filter {
            (type == null || it.type == type) && (status == null || it.status == status)
        }

    fun update(id: Long, updates: Map<String, String>): Boolean {
        val instrument = getById(id) ?: return false

        updates.forEach { (key, value) ->
            when (key.lowercase()) {
                "name" -> {
                    InstrumentValidator.validateName(value)
                    instrument.name = value
                }
                "location" -> {
                    InstrumentValidator.validateLocation(value)
                    instrument.location = value
                }
                "status" -> {
                    try {
                        instrument.status = InstrumentStatus.valueOf(value.uppercase())
                    } catch (e: Exception) {
                        println("Invalid status: $value")
                    }
                }
                else -> println("Unknown field: $key")
            }
        }

        instrument.updatedAt = Instant.now()
        return true
    }

    fun remove(id: Long) {
        calibrationService.removeByInstrumentId(id) // здесь используем метод из CalibrationService
        maintenanceService.removeById(id)
        if (instruments.remove(id) == null) {
            throw NoSuchElementException("Ошибка: прибор с id=$id не найден")
        }
    }

    fun listCalibrations(instrumentId: Long): List<Calibration> =
        calibrationService.listForInstrument(instrumentId)

    fun getByTypeOrStatus(type: InstrumentType?, status: InstrumentStatus?): Collection<Instrument> {
        var result: Collection<Instrument> = instruments.values
        if (type != null) result = result.filter { it.type == type }
        if (status != null) result = result.filter { it.status == status }
        return result
    }

    fun listMaintenances(instrumentId: Long) =
        maintenanceService.listByInstrument(instrumentId)
}