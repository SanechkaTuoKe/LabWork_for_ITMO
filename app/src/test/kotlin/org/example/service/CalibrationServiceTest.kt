package org.example.service

import org.example.domain.CalibrationResult
import org.example.domain.InstrumentType
import org.example.domain.InstrumentStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CalibrationServiceTest {

    private lateinit var instrumentService: InstrumentService
    private lateinit var calibrationService: CalibrationService
    private var instrumentId: Long = 0

    @BeforeEach
    fun setup() {
        instrumentService = InstrumentService()
        instrumentId = instrumentService.add(
            name = "pH meter Mettler A1",
            type = InstrumentType.PH_METER,
            inventoryNumber = "INV-00077",  // исправлено: inventoryNumber вместо inventory
            location = "Lab-2 bench"
        ).id  // добавлено .id, так как add возвращает Instrument
        calibrationService = CalibrationService(instrumentService)
    }

    @Test
    fun addCalibration_invalidType_throwsException() {
        val invalidType = "INVALID_TYPE" // используем действительно невалидный тип

        val exception = assertThrows<IllegalArgumentException> {
            calibrationService.add(
                instrumentId = instrumentId,
                typeInput = invalidType,
                resultInput = CalibrationResult.OK.name,  // resultInput ожидает строку
                comment = "Test comment"
            )
        }

        assert(exception.message!!.contains("Invalid calibration type"))
    }
}