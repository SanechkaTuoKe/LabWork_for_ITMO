package org.example.storage

import org.example.service.CalibrationService
import org.example.service.InstrumentService
import org.example.service.MaintenanceService
import org.example.storage.saveLoad.CalibrationSaveLoad
import org.example.storage.saveLoad.InstrumentSaveLoad
import org.example.storage.saveLoad.MaintenanceSaveLoad
import org.example.storage.storageValidation.CalibrationStorageValidator
import org.example.storage.storageValidation.InstrumentStorageValidator
import org.example.storage.storageValidation.MaintenanceStorageValidator
import java.nio.file.Paths

class StorageService(
    private val instrumentService: InstrumentService,
    private val calibrationService: CalibrationService,
    private val maintenanceService: MaintenanceService
) {
    fun save(directory: String) {
        val dir = Paths.get(directory)

        InstrumentSaveLoad.create(dir.resolve("instruments.csv"))
            .save(instrumentService.getAll())

        CalibrationSaveLoad.create(dir.resolve("calibrations.csv"))
            .save(calibrationService.getAll())

        MaintenanceSaveLoad.create(dir.resolve("maintenances.csv"))
            .save(maintenanceService.getAll())
    }

    fun load(directory: String) {
        val dir = Paths.get(directory)
        val errors = mutableListOf<String>()

        // --- Загружаем инструменты ---
        val loadedInstruments = InstrumentSaveLoad.create(dir.resolve("instruments.csv")).load()
        loadedInstruments.values.forEach { inst ->
            val err = InstrumentStorageValidator.validate(inst)
            if (err != null) errors += err
        }
        if (errors.isNotEmpty()) throw IllegalArgumentException(
            "Load validation failed:\n" + errors.joinToString("\n")
        )
        instrumentService.loadAll(loadedInstruments)

        // --- Загружаем калибровки ---
        val loadedCalibrations = CalibrationSaveLoad.create(dir.resolve("calibrations.csv")).load()
        loadedCalibrations.values.forEach { cal ->
            val err = CalibrationStorageValidator.validate(cal)
            if (err != null) errors += err
        }
        if (errors.isNotEmpty()) throw IllegalArgumentException(
            "Load validation failed:\n" + errors.joinToString("\n")
        )
        calibrationService.loadAll(loadedCalibrations)

        // --- Загружаем обслуживания ---
        val loadedMaintenances = MaintenanceSaveLoad.create(dir.resolve("maintenances.csv")).load()
        loadedMaintenances.values.forEach { m ->
            val err = MaintenanceStorageValidator.validate(m)
            if (err != null) errors += err
        }
        if (errors.isNotEmpty()) throw IllegalArgumentException(
            "Load validation failed:\n" + errors.joinToString("\n")
        )
        maintenanceService.loadAll(loadedMaintenances)
    }
}
