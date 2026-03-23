package org.example.validation

import org.example.domain.Instrument

object InstrumentValidator {
    private const val MAX_NAME_LENGTH = 128
    private const val MAX_LOCATION_LENGTH = 64
    private const val MAX_INVENTORY_LENGTH = 32

    fun validateName(name: String) {
        require(name.isNotBlank()) { "Ошибка: название не может быть пустым" }
        require(name.length <= MAX_NAME_LENGTH) { "Ошибка: название должно быть до $MAX_NAME_LENGTH символов" }
    }

    fun validateLocation(location: String) {
        require(location.isNotBlank()) { "Ошибка: местоположение не может быть пустым" }
        require(location.length <= MAX_LOCATION_LENGTH) { "Ошибка: местоположение должно быть до $MAX_LOCATION_LENGTH символов" }
    }

    fun validateInventoryNumber(inv: String?) {
        inv?.let {
            require(it.length <= MAX_INVENTORY_LENGTH) { "Ошибка: инвентарный номер должен быть до $MAX_INVENTORY_LENGTH символов" }
        }
    }
}