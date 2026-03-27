package org.example.validation

import org.example.domain.MaintenanceType

object MaintenanceValidator {

    fun validateType(typeInput: String): MaintenanceType {
        return try {
            MaintenanceType.valueOf(typeInput.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Maintenance type must be SERVICE or REPAIR")
        }
    }

    fun validateDetails(details: String): String {
        if (details.isBlank()) {
            throw IllegalArgumentException("Details cannot be empty")
        }
        return details
    }
}