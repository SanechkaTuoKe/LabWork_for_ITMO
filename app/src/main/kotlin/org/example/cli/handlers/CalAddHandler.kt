package org.example.cli.handlers

import org.example.auth.UserService
import org.example.cli.services.ReaderService
import org.example.service.CalibrationService
import org.example.service.InstrumentService

class CalAddHandler(
    private val calibrationService: CalibrationService,
    private val userService: UserService
) : BaseHandler {

    private val reader = ReaderService()

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        return try {
            val username = userService.currentUsername
                ?: throw IllegalStateException("You must be logged in to add calibrations")

            val instrumentId = params.getOrNull(0)?.toLongOrNull()
                ?: throw IllegalArgumentException("Instrument ID must be a number")

            println("Enter calibration type (ONE_POINT/TWO_POINT):")
            val typeInput = reader.readCommand().joinToString("")

            println("Enter result (OK/FAIL):")
            val resultInput = reader.readCommand().joinToString("")

            println("Enter comment (optional, press Enter to skip):")
            val comment = reader.readCommand().joinToString(" ").takeIf { it.isNotBlank() }

            val calibration = calibrationService.add(
                instrumentId,
                typeInput,
                resultInput,
                comment,
                username
            )

            println("OK calibration_id=${calibration.id}")
            true

        } catch (e: Exception) {
            ErrorHandler.handle(e)
            true
        }
    }

    override fun help(): String =
        "cal_add <instrument_id> - Add calibration (requires login)"
}