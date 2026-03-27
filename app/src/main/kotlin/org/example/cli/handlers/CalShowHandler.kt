package org.example.cli.handlers

import org.example.service.CalibrationService
import org.example.service.InstrumentService
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CalShowHandler(
    private val calibrationService: CalibrationService
) : BaseHandler {

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        return try {
            val id = params.getOrNull(0)?.toLongOrNull()
                ?: throw IllegalArgumentException("Calibration ID must be a number")

            val cal = calibrationService.getById(id)

            val date = cal.calibratedAt
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            println("Calibration #${cal.id}")
            println("instrument_id: ${cal.instrumentId}")
            println("type: ${cal.type}")
            println("result: ${cal.result}")
            println("comment: ${cal.comment}")
            println("time: $date")

            true

        } catch (e: Exception) {
            ErrorHandler.handle(e)
            true
        }
    }

    override fun help(): String = "cal_show <calibration_id>"
}