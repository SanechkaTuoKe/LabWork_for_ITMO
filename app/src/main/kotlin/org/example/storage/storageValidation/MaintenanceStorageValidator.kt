package org.example.storage.storageValidation

import org.example.domain.Maintenance

object MaintenanceStorageValidator : StorageValidator<Maintenance> {
    override fun validate(entity: Maintenance): String? {
        if (entity.id <= 0)           return "Maintenance id=${entity.id}: invalid id"
        if (entity.instrumentId <= 0) return "Maintenance id=${entity.id}: invalid instrumentId"
        if (entity.details.isBlank()) return "Maintenance id=${entity.id}: details is blank"
        return null
    }
}
