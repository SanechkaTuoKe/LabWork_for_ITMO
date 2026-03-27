package org.example.cli.handlers

import org.example.cli.util.Param
import org.example.service.CalibrationService
import org.example.service.InstrumentService
import java.time.format.DateTimeFormatter

class CalListHandler(
    private val calibrationService: CalibrationService
) : BaseHandler {

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {

        val id = params.getOrNull(0)?.toLongOrNull()
        if (id == null) {
            println("Invalid instrument id")
            return true
        }

        val last = Param.paramValue(params, "last")?.toIntOrNull()

        val calibrations = calibrationService.listByInstrument(id, last)

        if (calibrations.isEmpty()) {
            println("No calibrations found")
            return true
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        println("Result  Date       ID  Type       Comment")

        for (cal in calibrations) {
            val result = cal.result.toString().padEnd(6)
            val date = cal.calibratedAt
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
                .format(formatter)

            println("${result} $date ${cal.id} ${cal.type} ${cal.comment}")
        }

        return true
    }

    override fun help(): String =
        "cal_list <instrument_id> [--last N]"
}