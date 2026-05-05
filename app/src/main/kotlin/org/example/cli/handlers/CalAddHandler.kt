package org.example.cli.handlers

import org.example.cli.services.ReaderService
import org.example.service.CalibrationService
import org.example.service.InstrumentService

class CalAddHandler(
    private val calibrationService: CalibrationService
) : BaseHandler {

    private val reader = ReaderService()

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        return try {
            val instrumentId = params.getOrNull(0)?.toLongOrNull()
                ?: throw IllegalArgumentException("Instrument ID must be a number")

            println("Enter calibration type (ONE_POINT/TWO_POINT):")
            val typeInput = reader.readCommand().joinToString("")

            println("Enter result (OK/FAIL):")
            val resultInput = reader.readCommand().joinToString("")

            println("Enter comment (optional):")
            val comment = reader.readCommand().joinToString(" ")

            val calibration = calibrationService.add(
                instrumentId,
                typeInput,
                resultInput,
                comment
            )

            println("OK calibration_id=${calibration.id}")
            true

        } catch (e: Exception) {
            ErrorHandler.handle(e)
            true
        }
    }

    override fun help(): String =
        "cal_add <instrument_id> - Add calibration for instrument"
}