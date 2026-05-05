package org.example.cli.handlers

import org.example.service.InstrumentService

class InstUpdateHandler : BaseHandler {

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        return try {
            val id = params.getOrNull(0)?.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid instrument ID")

            val updates = params.drop(1).associate {
                val (k, v) = it.split("=", limit = 2)
                k to v.trim('"')
            }

            val name = updates["name"]
            val location = updates["location"]
            val inventory = updates["inventoryNumber"]

            instrumentService.update(id, name, location, inventory)

            println("OK")
            true

        } catch (e: Exception) {
            ErrorHandler.handle(e)
            true
        }
    }

    override fun help(): String =
        "inst_update <id> name=... location=... inventoryNumber=..."
}