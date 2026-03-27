package org.example.validation
import org.example.domain.CalibrationResult

object CalibrationValidator {
    private const val MAX_COMMENT_LENGTH = 128

    fun validateResult(result: String) {
        if (result != "OK" && result != "FAIL") {
            throw IllegalArgumentException(
                "Ошибка: результат должен быть OK или FAIL"
            )
        }
    }

    fun validateComment(comment: String?) {
        if (comment != null && comment.length > MAX_COMMENT_LENGTH) {
            throw IllegalArgumentException(
                "Ошибка: комментарий должен быть до $MAX_COMMENT_LENGTH символов"
            )
        }
    }

    fun validateType(typeInput: String) {}
}