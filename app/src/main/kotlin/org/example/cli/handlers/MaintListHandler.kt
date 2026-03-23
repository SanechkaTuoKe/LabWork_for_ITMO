package org.example.cli.handlers

import org.example.cli.util.Param
import org.example.service.InstrumentService
import java.time.format.DateTimeFormatter

class MaintListHandler : BaseHandler {

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {

        val instrumentId = params.getOrNull(0)?.toLongOrNull()
        if (instrumentId == null) {
            println("Invalid instrument id")
            return true
        }

        val instrument = instrumentService.getById(instrumentId)
        if (instrument == null) {
            println("Instrument not found")
            return true
        }

        val lastStr = Param.paramValue(params, "last")
        val lastCount = lastStr?.toIntOrNull() ?: Int.MAX_VALUE

        val maintenances = instrumentService.listMaintenances(instrumentId) // создадим метод в InstrumentService
            .sortedByDescending { it.doneAt }
            .take(lastCount)

        if (maintenances.isEmpty()) {
            println("No maintenance records found")
            return true
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        println("Time       ID  Type      Details")
        for (m in maintenances) {
            val time = m.doneAt.atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(formatter)
            val idStr = m.id.toString().padEnd(3)
            val type = m.type.toString().padEnd(8)
            val details = m.details
            println("$time $idStr $type $details")
        }

        return true
    }

    override fun help(): String =
        "maint_list <instrument_id> [--last N]  - list last N maintenance records for instrument"
}