package org.example.storage

import java.time.Instant

class FileValidator {

    fun validate(
        instruments: List<CsvRow>,
        calibrations: List<CsvRow>,
        maintenances: List<CsvRow>
    ): List<String> {

        val errors = mutableListOf<String>()
        val instrumentIds = mutableSetOf<Long>()

        validateInstruments(instruments, errors, instrumentIds)
        validateCalibrations(calibrations, instrumentIds, errors)
        validateMaintenances(maintenances, instrumentIds, errors)

        return errors
    }

    private fun validateInstruments(
        rows: List<CsvRow>,
        errors: MutableList<String>,
        validIds: MutableSet<Long>
    ) {
        val seen = mutableSetOf<Long>()

        for (row in rows) {
            if (row.size != 9) {
                errors.add("Instrument: wrong number of fields")
                continue
            }

            val id = row.get(0).toLongOrNull()
            if (id == null) {
                errors.add("Instrument: invalid id")
                continue
            }

            if (!seen.add(id)) {
                errors.add("Instrument: duplicate id=$id")
            }

            if (row.get(1).isBlank()) errors.add("Instrument[$id]: name empty")
            if (row.get(4).isBlank()) errors.add("Instrument[$id]: location empty")

            val statusStr = row.get(5).trim().uppercase()

            val status = try {
                org.example.domain.InstrumentStatus.valueOf(statusStr)
            } catch (e: Exception) {
                errors.add("Instrument[$id]: invalid status '$statusStr'")
                null
            }

            if (!parseInstant(row.get(7))) errors.add("Instrument[$id]: bad createdAt")
            if (!parseInstant(row.get(8))) errors.add("Instrument[$id]: bad updatedAt")

            validIds.add(id)
        }
    }

    private fun validateCalibrations(
        rows: List<CsvRow>,
        validInstrumentIds: Set<Long>,
        errors: MutableList<String>
    ) {
        for (row in rows) {
            if (row.size != 5) {
                errors.add("Calibration: wrong fields")
                continue
            }

            val instrId = row.get(1).toLongOrNull()
            if (instrId == null || !validInstrumentIds.contains(instrId)) {
                errors.add("Calibration: invalid instrumentId")
            }

            if (!parseInstant(row.get(4))) {
                errors.add("Calibration: bad date")
            }
        }
    }

    private fun validateMaintenances(
        rows: List<CsvRow>,
        validInstrumentIds: Set<Long>,
        errors: MutableList<String>
    ) {
        for (row in rows) {
            if (row.size != 5) {
                errors.add("Maintenance: wrong fields")
                continue
            }

            val instrId = row.get(1).toLongOrNull()
            if (instrId == null || !validInstrumentIds.contains(instrId)) {
                errors.add("Maintenance: invalid instrumentId")
            }

            if (row.get(3).isBlank()) {
                errors.add("Maintenance: empty details")
            }

            if (!parseInstant(row.get(4))) {
                errors.add("Maintenance: bad date")
            }
        }
    }

    private fun parseInstant(str: String): Boolean {
        return try {
            Instant.parse(str)
            true
        } catch (e: Exception) {
            false
        }
    }
}