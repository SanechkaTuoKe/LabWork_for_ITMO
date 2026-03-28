package org.example.cli.handlers

import org.example.cli.services.ReaderService
import org.example.service.MaintenanceService
import org.example.service.InstrumentService
import org.example.validation.MaintenanceValidator

class MaintAddHandler(
    private val maintenanceService: MaintenanceService
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

            println("Enter type (SERVICE/REPAIR):")
            val typeInput = reader.readCommand().joinToString("")
            val type = MaintenanceValidator.validateType(typeInput)

            println("Enter details:")
            val details = reader.readCommand().joinToString(" ")

            val maintenance = maintenanceService.add(instrumentId, type, details)

            println("OK maintenance_id=${maintenance.id}")
            true

        } catch (e: Exception) {
            ErrorHandler.handle(e)
            true
        }
    }

    override fun help(): String =
        "maint_add <instrument_id>"
}