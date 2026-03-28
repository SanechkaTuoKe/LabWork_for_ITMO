package org.example.cli.handlers

import org.example.cli.util.Param
import org.example.service.CalibrationService
import org.example.service.InstrumentService
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class InstDueHandler(
    private val calibrationService: CalibrationService
) : BaseHandler {

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        return try {
            val days = Param.paramValue(params, "days")?.toLongOrNull()
                ?: throw IllegalArgumentException("--days required")

            val now = LocalDate.now()

            instrumentService.getAll().forEach { instrument ->
                val last = calibrationService
                    .listByInstrument(instrument.id)
                    .maxByOrNull { it.calibratedAt }

                val lastDate = last?.calibratedAt
                    ?.atZone(ZoneId.systemDefault())
                    ?.toLocalDate()

                if (lastDate == null || ChronoUnit.DAYS.between(lastDate, now) > days) {
                    println("${instrument.id}  ${lastDate ?: "N/A"}  ${instrument.status}")
                }
            }

            true
        } catch (e: Exception) {
            ErrorHandler.handle(e)
            true
        }
    }

    override fun help(): String =
        "inst_due --days N"
}