package org.example.storage

import org.example.domain.*
import java.time.Instant
import java.util.TreeMap

class StorageService(
    private val storage: FileStorage,
    private val validator: FileValidator
) {
    // Изменено на var, чтобы можно было переназначить ссылку в attachCollections
    private var instruments: TreeMap<Long, Instrument> = TreeMap()
    private var calibrations: TreeMap<Long, Calibration> = TreeMap()
    private var maintenances: TreeMap<Long, Maintenance> = TreeMap()

    fun getInstruments(): Map<Long, Instrument> = instruments
    fun getCalibrations(): Map<Long, Calibration> = calibrations
    fun getMaintenances(): Map<Long, Maintenance> = maintenances

    fun save(path: String) {
        storage.saveAll(path, instruments, calibrations, maintenances)
    }

    fun load(path: String) {
        val (iRows, cRows, mRows) = storage.loadAll(path)

        val errors = validator.validate(iRows, cRows, mRows)
        if (errors.isNotEmpty()) {
            throw IllegalArgumentException(errors.joinToString("\n"))
        }

        val newInstr = TreeMap<Long, Instrument>()
        val newCal = TreeMap<Long, Calibration>()
        val newMaint = TreeMap<Long, Maintenance>()

        try {
            for (r in iRows) newInstr[parseInstrument(r).id] = parseInstrument(r)
            for (r in cRows) newCal[parseCalibration(r).id] = parseCalibration(r)
            for (r in mRows) newMaint[parseMaintenance(r).id] = parseMaintenance(r)
        } catch (e: Exception) {
            throw IllegalArgumentException("Parse error: ${e.message}")
        }

        instruments.clear()
        instruments.putAll(newInstr)
        calibrations.clear()
        calibrations.putAll(newCal)
        maintenances.clear()
        maintenances.putAll(newMaint)
    }

    // Исправлено:
    private fun parseInstrument(r: CsvRow) = Instrument(
        id = r.fields[0].toLong(),
        name = r.fields[1],
        type = InstrumentType.valueOf(r.fields[2]),
        inventoryNumber = r.fields[3].ifBlank { null },
        location = r.fields[4],
        status = InstrumentStatus.valueOf(r.fields[5]),
        ownerUsername = r.fields[6],
        createdAt = Instant.parse(r.fields[7]),
        updatedAt = Instant.parse(r.fields[8])
    )

    private fun parseCalibration(r: CsvRow) = Calibration(
        id = r.fields[0].toLong(),
        instrumentId = r.fields[1].toLong(),
        type = CalibrationType.valueOf(r.fields[2]),
        result = CalibrationResult.valueOf(r.fields[3]),
        comment = r.fields[4],
        calibratedAt = Instant.parse(r.fields[5]),
        ownerUsername = r.fields[6],
        createdAt = Instant.parse(r.fields[7])
    )

    private fun parseMaintenance(row: CsvRow): Maintenance {
        return Maintenance(
            id = row.fields[0].toLong(),
            instrumentId = row.fields[1].toLong(),
            type = MaintenanceType.valueOf(row.fields[2]),
            details = row.fields[3],
            doneAt = Instant.parse(row.fields[4]),
            ownerUsername = row.fields[5],
            createdAt = Instant.parse(row.fields[6])
        )
    }

    // Исправлено: добавлен перенос строки
    fun attachCollections(
        instrs: TreeMap<Long, Instrument>,
        cals: TreeMap<Long, Calibration>,
        maints: TreeMap<Long, Maintenance>
    ) {
        this.instruments = instrs
        this.calibrations = cals
        this.maintenances = maints
    }
}