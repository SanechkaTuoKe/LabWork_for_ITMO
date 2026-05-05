package org.example.validation

import org.example.domain.CalibrationResult
import org.example.domain.CalibrationType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CalibrationValidatorTest {

    @Test
    fun `validateType - should return type for valid input`() {
        assertEquals(CalibrationType.ONE_POINT, CalibrationValidator.validateType("ONE_POINT"))
        assertEquals(CalibrationType.TWO_POINT, CalibrationValidator.validateType("TWO_POINT"))
    }

    @Test
    fun `validateType - should handle lowercase`() {
        assertEquals(CalibrationType.ONE_POINT, CalibrationValidator.validateType("one_point"))
    }

    @Test
    fun `validateType - should throw for invalid type`() {
        assertThrows<IllegalArgumentException> { CalibrationValidator.validateType("INVALID") }
    }

    @Test
    fun `validateResult - should return result for valid input`() {
        assertEquals(CalibrationResult.OK, CalibrationValidator.validateResult("OK"))
        assertEquals(CalibrationResult.FAIL, CalibrationValidator.validateResult("FAIL"))
    }

    @Test
    fun `validateResult - should throw for invalid result`() {
        assertThrows<IllegalArgumentException> { CalibrationValidator.validateResult("INVALID") }
    }

    @Test
    fun `validateComment - should return trimmed comment`() {
        assertEquals("test", CalibrationValidator.validateComment("  test  "))
        assertEquals("", CalibrationValidator.validateComment(null))
        assertEquals("", CalibrationValidator.validateComment("   "))
    }
}