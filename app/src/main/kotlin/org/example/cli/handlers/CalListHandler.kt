package org.example.cli.handlers

import org.example.cli.util.Param
import org.example.service.InstrumentService
import java.time.format.DateTimeFormatter

class CalListHandler : BaseHandler {

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

        val instrument = instrumentService.getById(id)
        if (instrument == null) {
            println("Instrument not found")
            return true
        }

        // Опциональный параметр --last N
        val lastStr = Param.paramValue(params, "last")
        val lastCount = lastStr?.toIntOrNull() ?: Int.MAX_VALUE

        // Получаем калибровки через InstrumentService
        val calibrations = instrumentService.listCalibrations(id)
            .sortedByDescending { it.calibratedAt } // теперь поле calibratedAt
            .take(lastCount)

        if (calibrations.isEmpty()) {
            println("No calibrations found")
            return true
        }

        // Вывод таблицы
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        println("Result  Time       ID  Type       Comment")
        for (cal in calibrations) {
            val result = cal.result.padEnd(6)
            val date = cal.calibratedAt.atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(formatter)
            val idStr = cal.id.toString().padEnd(3)
            val type = cal.type.toString().padEnd(10)
            val comment = cal.comment
            println("$result $date $idStr $type $comment")
        }

        return true
    }

    override fun help(): String =
        "CalList <instrument_id> [--last N]  - list last N calibrations for instrument"
}