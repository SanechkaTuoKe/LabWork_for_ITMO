package org.example.validation

import org.example.domain.MaintenanceType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class MaintenanceValidatorTest {

    @Test
    fun `validateType - should return MaintenanceType for valid type`() {
        assertEquals(
            MaintenanceType.SERVICE,
            MaintenanceValidator.validateType("SERVICE")
        )
        assertEquals(
            MaintenanceType.REPAIR,
            MaintenanceValidator.validateType("REPAIR")
        )
    }

    @Test
    fun `validateType - should handle lowercase input`() {
        assertEquals(
            MaintenanceType.SERVICE,
            MaintenanceValidator.validateType("service")
        )
        assertEquals(
            MaintenanceType.REPAIR,
            MaintenanceValidator.validateType("repair")
        )
    }

    @Test
    fun `validateType - should handle mixed case input`() {
        assertEquals(
            MaintenanceType.SERVICE,
            MaintenanceValidator.validateType("SeRvIcE")
        )
        assertEquals(
            MaintenanceType.REPAIR,
            MaintenanceValidator.validateType("RePaIr")
        )
    }

    @Test
    fun `validateType - should throw exception for invalid type`() {
        val exception = assertThrows<IllegalArgumentException> {
            MaintenanceValidator.validateType("CALIBRATION")
        }
        assertEquals(
            "Maintenance type must be SERVICE or REPAIR",
            exception.message
        )
    }

    @Test
    fun `validateType - should throw exception for empty string`() {
        val exception = assertThrows<IllegalArgumentException> {
            MaintenanceValidator.validateType("")
        }
        assertEquals(
            "Maintenance type must be SERVICE or REPAIR",
            exception.message
        )
    }

    @Test
    fun `validateType - should throw exception for blank string`() {
        val exception = assertThrows<IllegalArgumentException> {
            MaintenanceValidator.validateType("   ")
        }
        assertEquals(
            "Maintenance type must be SERVICE or REPAIR",
            exception.message
        )
    }

    // ========== TESTS FOR validateDetails ==========

    @Test
    fun `validateDetails - should return details for valid input`() {
        val details = "Regular maintenance check"
        assertEquals(
            details,
            MaintenanceValidator.validateDetails(details)
        )
    }

    @Test
    fun `validateDetails - should accept non-empty string with spaces`() {
        val details = "  Calibration and cleaning  "
        assertEquals(
            details,
            MaintenanceValidator.validateDetails(details)
        )
    }

    @Test
    fun `validateDetails - should throw exception for empty string`() {
        val exception = assertThrows<IllegalArgumentException> {
            MaintenanceValidator.validateDetails("")
        }
        assertEquals("Details cannot be empty", exception.message)
    }

    @Test
    fun `validateDetails - should throw exception for blank string`() {
        val exception = assertThrows<IllegalArgumentException> {
            MaintenanceValidator.validateDetails("   ")
        }
        assertEquals("Details cannot be empty", exception.message)
    }

    @Test
    fun `validateDetails - should throw exception for string with only whitespace`() {
        val exception = assertThrows<IllegalArgumentException> {
            MaintenanceValidator.validateDetails("\t\n\r")
        }
        assertEquals("Details cannot be empty", exception.message)
    }
}