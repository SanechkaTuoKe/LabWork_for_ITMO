package org.example.service

import org.example.domain.InstrumentType
import org.example.domain.CalibrationResult
import org.example.domain.CalibrationType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CalibrationServiceTest {

    private lateinit var instrumentService: InstrumentService
    private lateinit var calibrationService: CalibrationService
    private var instrumentId: Long = 0

    @BeforeEach
    fun setup() {
        instrumentService = InstrumentService()
        val instrument = instrumentService.add(
            name = "pH Meter",
            type = InstrumentType.PH_METER,
            inventoryNumber = "INV-001",
            location = "Lab-1"
        )
        instrumentId = instrument.id
        calibrationService = CalibrationService(instrumentService)
    }

    @Test
    fun `add - should add calibration`() {
        val calibration = calibrationService.add(
            instrumentId = instrumentId,
            typeInput = "ONE_POINT",
            resultInput = "OK",
            comment = "Test comment"
        )

        assertNotNull(calibration)
        assertEquals(instrumentId, calibration.instrumentId)
        assertEquals(CalibrationType.ONE_POINT, calibration.type)
        assertEquals(CalibrationResult.OK, calibration.result)
        assertEquals("Test comment", calibration.comment)
    }

    @Test
    fun `add - should handle empty comment`() {
        val calibration = calibrationService.add(
            instrumentId = instrumentId,
            typeInput = "TWO_POINT",
            resultInput = "FAIL",
            comment = null
        )

        assertNotNull(calibration)
        assertEquals("", calibration.comment)
    }

    @Test
    fun `add - should throw exception for non-existent instrument`() {
        assertThrows<IllegalArgumentException> {
            calibrationService.add(99999, "ONE_POINT", "OK")
        }
    }

    @Test
    fun `listByInstrument - should return calibrations`() {
        calibrationService.add(instrumentId, "ONE_POINT", "OK")
        calibrationService.add(instrumentId, "TWO_POINT", "FAIL")

        val list = calibrationService.listByInstrument(instrumentId)

        assertEquals(2, list.size)
    }

    @Test
    fun `listByInstrument - should return last N calibrations`() {
        calibrationService.add(instrumentId, "ONE_POINT", "OK")
        calibrationService.add(instrumentId, "TWO_POINT", "FAIL")
        calibrationService.add(instrumentId, "ONE_POINT", "OK")

        val last2 = calibrationService.listByInstrument(instrumentId, 2)

        assertEquals(2, last2.size)
    }
}