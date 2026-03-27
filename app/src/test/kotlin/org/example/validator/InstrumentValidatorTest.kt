package org.example.validation

import org.example.domain.InstrumentType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class InstrumentValidatorTest {

    @Test
    fun `validateName - should accept valid name`() {
        assertDoesNotThrow { InstrumentValidator.validateName("Valid Name") }
    }

    @Test
    fun `validateName - should throw for blank name`() {
        assertThrows<IllegalArgumentException> { InstrumentValidator.validateName("") }
        assertThrows<IllegalArgumentException> { InstrumentValidator.validateName("   ") }
    }

    @Test
    fun `validateType - should return type for valid number`() {
        val type = InstrumentValidator.validateType(1)
        assertEquals(InstrumentType.PH_METER, type)
    }

    @Test
    fun `validateType - should throw for invalid number`() {
        assertThrows<IllegalArgumentException> { InstrumentValidator.validateType(0) }
        assertThrows<IllegalArgumentException> { InstrumentValidator.validateType(99) }
    }

    @Test
    fun `validateInventoryNumber - should accept valid inventory`() {
        assertDoesNotThrow { InstrumentValidator.validateInventoryNumber("INV-001") }
    }

    @Test
    fun `validateInventoryNumber - should throw for blank`() {
        assertThrows<IllegalArgumentException> { InstrumentValidator.validateInventoryNumber("") }
    }

    @Test
    fun `validateLocation - should accept valid location`() {
        assertDoesNotThrow { InstrumentValidator.validateLocation("Lab-1") }
    }

    @Test
    fun `validateLocation - should throw for blank`() {
        assertThrows<IllegalArgumentException> { InstrumentValidator.validateLocation("") }
    }
}