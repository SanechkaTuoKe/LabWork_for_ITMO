package org.example.validation

import org.example.domain.MaintenanceType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class MaintenanceValidatorTest {

    @Test
    fun `validateType - should return type for valid input`() {
        assertEquals(MaintenanceType.SERVICE, MaintenanceValidator.validateType("SERVICE"))
        assertEquals(MaintenanceType.REPAIR, MaintenanceValidator.validateType("REPAIR"))
    }

    @Test
    fun `validateType - should handle lowercase`() {
        assertEquals(MaintenanceType.SERVICE, MaintenanceValidator.validateType("service"))
    }

    @Test
    fun `validateType - should throw for invalid type`() {
        assertThrows<IllegalArgumentException> { MaintenanceValidator.validateType("INVALID") }
    }

    @Test
    fun `validateDetails - should return details for valid input`() {
        val details = "Valid details"
        assertEquals(details, MaintenanceValidator.validateDetails(details))
    }

    @Test
    fun `validateDetails - should throw for blank details`() {
        assertThrows<IllegalArgumentException> { MaintenanceValidator.validateDetails("") }
        assertThrows<IllegalArgumentException> { MaintenanceValidator.validateDetails("   ") }
    }
}