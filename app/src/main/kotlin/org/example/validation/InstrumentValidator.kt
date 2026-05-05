package org.example.validation

import org.example.domain.InstrumentType

object InstrumentValidator {

    fun validateName(name: String) {
        if (name.isBlank()) {
            throw IllegalArgumentException("Name cannot be empty")
        }
        if (name.length > 128) {
            throw IllegalArgumentException("Name must be ≤ 128 characters")
        }
    }

    fun validateType(typeNum: Int): InstrumentType {
        if (typeNum !in 1..InstrumentType.values().size) {
            throw IllegalArgumentException(
                "Instrument type must be from 1 to ${InstrumentType.values().size}"
            )
        }
        return InstrumentType.values()[typeNum - 1]
    }

    fun validateInventoryNumber(inventory: String) {
        if (inventory.isBlank()) {
            throw IllegalArgumentException("Inventory number cannot be empty")
        }
        if (inventory.length > 32) {
            throw IllegalArgumentException("Inventory number must be ≤ 32 characters")
        }
    }

    fun validateLocation(location: String) {
        if (location.isBlank()) {
            throw IllegalArgumentException("Location cannot be empty")
        }
        if (location.length > 64) {
            throw IllegalArgumentException("Location must be ≤ 64 characters")
        }
    }
}