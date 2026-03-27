package org.example.validation

import org.example.domain.InstrumentType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class InstrumentValidatorTest {

    @Test
    fun `validateName - should accept valid name`() {
        assertDoesNotThrow {
            InstrumentValidator.validateName("pH meter Mettler A1")
        }
        assertDoesNotThrow {
            InstrumentValidator.validateName("Balance X200")
        }
        assertDoesNotThrow {
            InstrumentValidator.validateName("A") // минимальная длина
        }
    }

    @Test
    fun `validateName - should throw exception for blank name`() {
        val exception = assertThrows<IllegalArgumentException> {
            InstrumentValidator.validateName("")
        }
        assertEquals("Name cannot be empty", exception.message)

        assertThrows<IllegalArgumentException> {
            InstrumentValidator.validateName("   ")
        }
    }


    @Test
    fun `validateType - should return InstrumentType for valid type number`() {
        assertEquals(
            InstrumentType.PH_METER,
            InstrumentValidator.validateType(1)
        )
        assertEquals(
            InstrumentType.BALANCE,
            InstrumentValidator.validateType(2)
        )
        assertEquals(
            InstrumentType.SPECTROPHOTOMETER,
            InstrumentValidator.validateType(3)
        )
    }

    @Test
    fun `validateType - should throw exception for type number less than 1`() {
        val exception = assertThrows<IllegalArgumentException> {
            InstrumentValidator.validateType(0)
        }
        assertEquals(
            "Instrument type must be from 1 to ${InstrumentType.values().size}",
            exception.message
        )
    }

    @Test
    fun `validateType - should throw exception for type number greater than max`() {
        val maxType = InstrumentType.values().size
        val exception = assertThrows<IllegalArgumentException> {
            InstrumentValidator.validateType(maxType + 1)
        }
        assertEquals(
            "Instrument type must be from 1 to $maxType",
            exception.message
        )
    }

    @Test
    fun `validateInventoryNumber - should accept valid inventory number`() {
        assertDoesNotThrow {
            InstrumentValidator.validateInventoryNumber("INV-00077")
        }
        assertDoesNotThrow {
            InstrumentValidator.validateInventoryNumber("SN-12345")
        }
        assertDoesNotThrow {
            InstrumentValidator.validateInventoryNumber("ABC-001")
        }
    }

    @Test
    fun `validateInventoryNumber - should throw exception for blank inventory number`() {
        val exception = assertThrows<IllegalArgumentException> {
            InstrumentValidator.validateInventoryNumber("")
        }
        assertEquals("Inventory number cannot be empty", exception.message)

        assertThrows<IllegalArgumentException> {
            InstrumentValidator.validateInventoryNumber("   ")
        }
    }

    @Test
    fun `validateLocation - should accept valid location`() {
        assertDoesNotThrow {
            InstrumentValidator.validateLocation("Lab-2 bench")
        }
        assertDoesNotThrow {
            InstrumentValidator.validateLocation("Room 101")
        }
        assertDoesNotThrow {
            InstrumentValidator.validateLocation("Storage A-12")
        }
    }

    @Test
    fun `validateLocation - should throw exception for blank location`() {
        val exception = assertThrows<IllegalArgumentException> {
            InstrumentValidator.validateLocation("")
        }
        assertEquals("Location cannot be empty", exception.message)

        assertThrows<IllegalArgumentException> {
            InstrumentValidator.validateLocation("   ")
        }
    }
}