package org.example.validation

import org.example.domain.CalibrationResult
import org.example.domain.CalibrationType

object CalibrationValidator {

    fun validateType(input: String): CalibrationType {
        return try {
            CalibrationType.valueOf(input.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Invalid calibration type. Allowed: ONE_POINT, TWO_POINT"
            )
        }
    }

    fun validateResult(input: String): CalibrationResult {
        return try {
            CalibrationResult.valueOf(input.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Invalid calibration result. Allowed: OK, FAIL"
            )
        }
    }

    fun validateComment(comment: String?): String {
        val trimmed = comment?.trim().orEmpty()
        if (trimmed.length > 128) {
            throw IllegalArgumentException("Comment must be ≤ 128 characters")
        }
        return trimmed
    }
}