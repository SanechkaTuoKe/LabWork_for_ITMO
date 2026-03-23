package org.example.validation

object MaintenanceValidator {
    private const val MAX_DETAILS_LENGTH = 128

    fun validateDetails(details: String) {
        require(details.isNotBlank()) { "Ошибка: описание не может быть пустым" }
        require(details.length <= MAX_DETAILS_LENGTH) {
            "Ошибка: описание должно быть до $MAX_DETAILS_LENGTH символов"
        }
    }
}