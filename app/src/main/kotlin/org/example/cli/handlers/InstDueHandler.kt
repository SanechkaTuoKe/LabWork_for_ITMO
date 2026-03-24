package org.example.cli.handlers

import org.example.cli.util.Param
import org.example.domain.InstrumentStatus
import org.example.service.InstrumentService
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class InstDueHandler : BaseHandler {

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {

        val daysStr = Param.paramValue(params, "days")
        val days = daysStr?.toLongOrNull()
        if (days == null) {
            println("Error: --days must be a number")
            return true
        }

        val now = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        println("Instrument  Last calibration  Status")
        val instruments = instrumentService.getAll()
        for (instrument in instruments) {
            val lastCalibration = instrumentService.listCalibrations(instrument.id)
                .maxByOrNull { it.calibratedAt }
            val lastDate = lastCalibration?.calibratedAt?.atZone(ZoneId.systemDefault())?.toLocalDate()
            if (lastDate != null) {
                val diffDays = java.time.temporal.ChronoUnit.DAYS.between(lastDate, now)
                if (diffDays > days) {
                    val dateStr = lastDate.format(formatter).padEnd(15)
                    println("${instrument.id.toString().padEnd(11)} $dateStr ${instrument.status}")
                }
            } else {
                // если калибровок нет, тоже показываем прибор
                println("${instrument.id.toString().padEnd(11)} ${"N/A".padEnd(15)} ${instrument.status}")
            }
        }

        return true
    }

    override fun help(): String =
        "InstDue [--days N]  - show instruments which last calibration is older than N days"
}