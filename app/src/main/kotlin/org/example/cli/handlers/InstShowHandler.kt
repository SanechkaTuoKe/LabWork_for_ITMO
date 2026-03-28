package org.example.cli.handlers

import org.example.service.CalibrationService
import org.example.service.InstrumentService
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class InstShowHandler(
    private val calibrationService: CalibrationService
) : BaseHandler {

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        return try {
            val id = params.getOrNull(0)?.toLongOrNull()
                ?: throw IllegalArgumentException("Instrument id must be a number")

            val instrument = instrumentService.getById(id)
                ?: throw IllegalArgumentException("Instrument not found")

            val lastCal = calibrationService
                .listByInstrument(id)
                .maxByOrNull { it.calibratedAt }

            val lastCalStr = lastCal?.calibratedAt
                ?.atZone(ZoneId.systemDefault())
                ?.toLocalDate()
                ?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ?: "N/A"

            println("Instrument #${instrument.id}")
            println("name: ${instrument.name}")
            println("type: ${instrument.type}")
            println("inventory: ${instrument.inventoryNumber}")
            println("location: ${instrument.location}")
            println("status: ${instrument.status}")
            println("last_calibration: $lastCalStr")

            true

        } catch (e: Exception) {
            ErrorHandler.handle(e)
            true
        }
    }

    override fun help(): String = "inst_show <id>"
}