package org.example.service

import org.example.domain.*
import org.example.storage.saveLoad.CalibrationSaveLoad
import org.example.validation.CalibrationValidator
import java.time.Instant
import java.util.*
import org.example.storage.saveLoad.InstrumentSaveLoad
import java.sql.Connection

class CalibrationService(
    private val instrumentService: InstrumentService,
    private val connection: Connection? = null
) {
    private val calibrations = TreeMap<Long, Calibration>()
    private var nextId = 1L
    private val dbStorage = connection?.let { CalibrationSaveLoad.create(it) }

    init {
        connection?.let {
            loadFromDatabase()
        }
    }

    public fun loadFromDatabase() {
        dbStorage?.let { storage ->
            val loaded = storage.load()
            calibrations.clear()
            calibrations.putAll(loaded)
            nextId = if (loaded.isEmpty()) 1L else loaded.keys.max() + 1L
        }
    }

    fun add(
        instrumentId: Long,
        typeInput: String,
        resultInput: String,
        comment: String? = null,
        ownerUsername: String = "SYSTEM",
        calibratedAt: Instant = Instant.now()
    ): Calibration {
        val instrument = instrumentService.getById(instrumentId)
            ?: throw IllegalArgumentException("Instrument with id=$instrumentId not found")
        if (instrument.status != InstrumentStatus.ACTIVE)
            throw IllegalArgumentException("Instrument is not ACTIVE")

        val type = CalibrationValidator.validateType(typeInput)
        val result = CalibrationValidator.validateResult(resultInput)
        val validatedComment = CalibrationValidator.validateComment(comment)

        if (dbStorage != null) {
            val tempCalibration = Calibration(
                id = 0,
                instrumentId = instrument.id,
                type = type,
                result = result,
                comment = validatedComment,
                calibratedAt = calibratedAt,
                ownerUsername = ownerUsername,
                createdAt = Instant.now()
            )
            val generatedId = dbStorage!!.insert(tempCalibration)
            if (generatedId != null) {
                val calibration = Calibration(
                    id = generatedId,
                    instrumentId = tempCalibration.instrumentId,
                    type = tempCalibration.type,
                    result = tempCalibration.result,
                    comment = tempCalibration.comment,
                    calibratedAt = tempCalibration.calibratedAt,
                    ownerUsername = tempCalibration.ownerUsername,
                    createdAt = tempCalibration.createdAt
                )
                calibrations[calibration.id] = calibration
                return calibration
            }
        }
        val calibration = Calibration(
            id = nextId++,
            instrumentId = instrument.id,
            type = type,
            result = result,
            comment = validatedComment,
            calibratedAt = calibratedAt,
            ownerUsername = ownerUsername,
            createdAt = Instant.now()
        )
        calibrations[calibration.id] = calibration
        return calibration
    }

    fun getById(id: Long): Calibration =
        calibrations[id] ?: throw NoSuchElementException("Calibration with id=$id not found")

    fun getAll(): List<Calibration> = calibrations.values.toList()

    fun listByInstrument(instrumentId: Long, last: Int? = null): List<Calibration> {
        require(instrumentService.getById(instrumentId) != null) {
            "Instrument with id=$instrumentId not found"
        }
        val list = calibrations.values
            .filter { it.instrumentId == instrumentId }
            .sortedByDescending { it.calibratedAt }
        return if (last != null && last > 0) list.take(last) else list
    }

    fun removeByInstrumentId(instrumentId: Long) {
        dbStorage?.let { storage ->
            calibrations.values.filter { it.instrumentId == instrumentId }.forEach {
                storage.delete(it.id)
            }
        }
        calibrations.entries.removeIf { it.value.instrumentId == instrumentId }
    }



    fun loadAll(loaded: Map<Long, Calibration>) {
        calibrations.clear()
        calibrations.putAll(loaded)
        nextId = if (loaded.isEmpty()) 1L else loaded.keys.max() + 1L
    }

    fun addExisting(calibration: Calibration) {
        calibrations[calibration.id] = calibration
        if (calibration.id >= nextId) {
            nextId = calibration.id + 1
        }
    }

    fun clearAll() {
        calibrations.clear()
        nextId = 1L
    }
    internal fun getAllCalibrations(): TreeMap<Long, Calibration> = calibrations
}
