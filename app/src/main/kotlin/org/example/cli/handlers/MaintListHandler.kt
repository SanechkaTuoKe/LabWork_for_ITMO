package org.example.cli.handlers

import org.example.cli.util.Param
import org.example.service.MaintenanceService
import org.example.service.InstrumentService
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MaintListHandler(
    private val maintenanceService: MaintenanceService
) : BaseHandler {

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        return try {
            val instrumentId = params.getOrNull(0)?.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid instrument id")

            val last = Param.paramValue(params, "last")?.toIntOrNull()

            val list = maintenanceService.listByInstrument(instrumentId, last)

            if (list.isEmpty()) {
                println("No maintenance records")
                return true
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            println("ID  TYPE  DATE  DETAILS")

            list.forEach {
                val date = it.doneAt
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(formatter)

                println("${it.id} ${it.type} $date ${it.details}")
            }

            true

        } catch (e: Exception) {
            ErrorHandler.handle(e)
            true
        }
    }

    override fun help(): String =
        "maint_list <instrument_id> [--last N]"
}