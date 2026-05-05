package org.example.storage.storageValidation

import org.example.domain.Calibration

object CalibrationStorageValidator : StorageValidator<Calibration> {
    override fun validate(entity: Calibration): String? {
        if (entity.id <= 0)             return "Calibration id=${entity.id}: invalid id"
        if (entity.instrumentId <= 0)   return "Calibration id=${entity.id}: invalid instrumentId"
        return null
    }
}
