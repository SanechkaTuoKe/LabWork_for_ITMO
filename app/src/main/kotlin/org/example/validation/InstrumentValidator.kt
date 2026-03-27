package org.example.validation

import org.example.domain.InstrumentType

object InstrumentValidator {

    fun validateName(name: String) {
        if (name.isBlank()) {
            throw IllegalArgumentException("Name cannot be empty")
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
    }

    fun validateLocation(location: String) {
        if (location.isBlank()) {
            throw IllegalArgumentException("Location cannot be empty")
        }
    }
}