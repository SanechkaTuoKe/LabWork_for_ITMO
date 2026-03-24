package org.example.cli.handlers

import org.example.cli.services.ReaderService
import org.example.domain.MaintenanceType
import org.example.service.InstrumentService
import org.example.service.MaintenanceService

class MaintAddHandler : BaseHandler {
    private val readerService = ReaderService()

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

        val maintenanceService = MaintenanceService(instrumentService)

        println("Type (SERVICE/REPAIR):")
        val typeInput = readerService.readCommand().joinToString("").uppercase()
        val type = try {
            MaintenanceType.valueOf(typeInput)
        } catch (e: Exception) {
            println("Invalid maintenance type")
            return true
        }

        println("What was done:")
        val description = readerService.readCommand().joinToString(" ").trim()
        if (description.isEmpty()) {
            println("Error: description cannot be empty")
            return true
        }

        // Вызов add с корректным параметром details
        val maintenance = maintenanceService.add(
            instrumentId = instrumentId,
            type = type,
            details = description
        )

        println("OK maintenance_id=${maintenance.id}")
        return true
    }

    override fun help(): String =
        "MaintAdd <instrument_id>  - Add maintenance/repair record interactively"
}