package org.example.storage.storageValidation

import org.example.domain.Instrument

object InstrumentStorageValidator : StorageValidator<Instrument> {
    override fun validate(entity: Instrument): String? {
        if (entity.name.isBlank())     return "Instrument id=${entity.id}: name is blank"
        if (entity.location.isBlank()) return "Instrument id=${entity.id}: location is blank"
        if (entity.id <= 0)            return "Instrument id=${entity.id}: invalid id"
        return null
    }
}
