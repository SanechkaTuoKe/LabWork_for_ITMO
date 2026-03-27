package org.example.validation

import org.example.domain.CalibrationResult
import org.example.domain.CalibrationType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CalibrationValidatorTest {

    @Test
    fun `validateType - should return CalibrationType for valid type`() {
        assertEquals(
            CalibrationType.ONE_POINT,
            CalibrationValidator.validateType("ONE_POINT")
        )
        assertEquals(
            CalibrationType.TWO_POINT,
            CalibrationValidator.validateType("TWO_POINT")
        )
    }

    @Test
    fun `validateType - should handle lowercase input`() {
        assertEquals(
            CalibrationType.ONE_POINT,
            CalibrationValidator.validateType("one_point")
        )
        assertEquals(
            CalibrationType.TWO_POINT,
            CalibrationValidator.validateType("two_point")
        )
    }

    @Test
    fun `validateType - should handle mixed case input`() {
        assertEquals(
            CalibrationType.ONE_POINT,
            CalibrationValidator.validateType("OnE_PoInT")
        )
    }

    @Test
    fun `validateType - should throw exception for invalid type`() {
        val exception = assertThrows<IllegalArgumentException> {
            CalibrationValidator.validateType("THREE_POINT")
        }
        assertEquals(
            "Invalid calibration type. Allowed: ONE_POINT, TWO_POINT",
            exception.message
        )
    }

    @Test
    fun `validateType - should throw exception for empty string`() {
        val exception = assertThrows<IllegalArgumentException> {
            CalibrationValidator.validateType("")
        }
        assertEquals(
            "Invalid calibration type. Allowed: ONE_POINT, TWO_POINT",
            exception.message
        )
    }

    @Test
    fun `validateType - should throw exception for blank string`() {
        val exception = assertThrows<IllegalArgumentException> {
            CalibrationValidator.validateType("   ")
        }
        assertEquals(
            "Invalid calibration type. Allowed: ONE_POINT, TWO_POINT",
            exception.message
        )
    }

    // ========== TESTS FOR validateResult ==========

    @Test
    fun `validateResult - should return CalibrationResult for valid result`() {
        assertEquals(
            CalibrationResult.OK,
            CalibrationValidator.validateResult("OK")
        )
        assertEquals(
            CalibrationResult.FAIL,
            CalibrationValidator.validateResult("FAIL")
        )
    }

    @Test
    fun `validateResult - should handle lowercase input`() {
        assertEquals(
            CalibrationResult.OK,
            CalibrationValidator.validateResult("ok")
        )
        assertEquals(
            CalibrationResult.FAIL,
            CalibrationValidator.validateResult("fail")
        )
    }

    @Test
    fun `validateResult - should handle mixed case input`() {
        assertEquals(
            CalibrationResult.OK,
            CalibrationValidator.validateResult("Ok")
        )
        assertEquals(
            CalibrationResult.FAIL,
            CalibrationValidator.validateResult("FaIl")
        )
    }

    @Test
    fun `validateResult - should throw exception for invalid result`() {
        val exception = assertThrows<IllegalArgumentException> {
            CalibrationValidator.validateResult("PARTIAL")
        }
        assertEquals(
            "Invalid calibration result. Allowed: OK, FAIL",
            exception.message
        )
    }

    @Test
    fun `validateResult - should throw exception for empty string`() {
        val exception = assertThrows<IllegalArgumentException> {
            CalibrationValidator.validateResult("")
        }
        assertEquals(
            "Invalid calibration result. Allowed: OK, FAIL",
            exception.message
        )
    }

    // ========== TESTS FOR validateComment ==========

    @Test
    fun `validateComment - should return trimmed comment for non-null input`() {
        assertEquals(
            "Test comment",
            CalibrationValidator.validateComment("Test comment")
        )
        assertEquals(
            "Test comment",
            CalibrationValidator.validateComment("  Test comment  ")
        )
    }

    @Test
    fun `validateComment - should return empty string for null input`() {
        assertEquals("", CalibrationValidator.validateComment(null))
    }

    @Test
    fun `validateComment - should return empty string for blank input`() {
        assertEquals("", CalibrationValidator.validateComment(""))
        assertEquals("", CalibrationValidator.validateComment("   "))
        assertEquals("", CalibrationValidator.validateComment("\t\n"))
    }

    @Test
    fun `validateComment - should never throw exception`() {
        assertDoesNotThrow {
            CalibrationValidator.validateComment(null)
            CalibrationValidator.validateComment("")
            CalibrationValidator.validateComment("Any text")
        }
    }
}