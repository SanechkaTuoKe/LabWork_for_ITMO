package org.example.cli.handlers

import org.example.service.MaintenanceService
import org.example.service.InstrumentService

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
                ?: throw IllegalArgumentException("Invalid id")

            val list = maintenanceService.listByInstrument(instrumentId)

            if (list.isEmpty()) {
                println("No maintenance records")
                return true
            }

            println("ID  TYPE  DATE  DETAILS")

            list.forEach {
                println("${it.id} ${it.type} ${it.doneAt} ${it.details}")
            }

            true

        } catch (e: Exception) {
            ErrorHandler.handle(e)
            true
        }
    }

    override fun help(): String =
        "maint_list <instrument_id>"
}