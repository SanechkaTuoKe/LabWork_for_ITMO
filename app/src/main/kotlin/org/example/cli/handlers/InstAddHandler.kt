package org.example.cli.handlers

import org.example.cli.services.ReaderService
import org.example.service.InstrumentService
import org.example.validation.InstrumentValidator

class InstAddHandler : BaseHandler {

    private val reader = ReaderService()

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        return try {

            println("Enter name:")
            val name = reader.readCommand().joinToString("")
            InstrumentValidator.validateName(name)

            println("Enter type (1-5):")
            val typeNum = reader.readCommand().joinToString("").toIntOrNull()
                ?: throw IllegalArgumentException("Invalid type")

            val type = InstrumentValidator.validateType(typeNum)

            println("Enter inventory (optional):")
            val inventory = reader.readCommand().joinToString("")
                .takeIf { it.isNotBlank() }

            println("Enter location:")
            val location = reader.readCommand().joinToString("")
            InstrumentValidator.validateLocation(location)

            val instrument = instrumentService.add(name, type, inventory, location)

            println("OK instrument_id=${instrument.id}")
            true

        } catch (e: Exception) {
            ErrorHandler.handle(e)
            true
        }
    }

    override fun help(): String =
        "inst_add"
}