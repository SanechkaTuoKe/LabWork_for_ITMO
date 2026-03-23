package org.example.cli.handlers

import org.example.cli.util.Param
import org.example.domain.Instrument
import org.example.domain.InstrumentStatus
import org.example.service.InstrumentService

class InstUpdateHandler : BaseHandler {

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

        if (params.size < 2) {
            println("No fields to update")
            return true
        }

        val instrument = instrumentService.getById(id)
        if (instrument == null) {
            println("Instrument not found")
            return true
        }

        val updates = mutableMapOf<String, String>()
        for (param in params.subList(1, params.size)) {
            val split = param.split("=", limit = 2)
            if (split.size != 2) {
                println("Invalid parameter format: $param")
                continue
            }
            val key = split[0].lowercase()
            val value = split[1].trim('"')

            when (key) {
                "name" -> instrument.name = value
                "location" -> instrument.location = value
                "status" -> {
                    try {
                        instrument.status = InstrumentStatus.valueOf(value.uppercase())
                    } catch (e: Exception) {
                        println("Invalid status: $value")
                    }
                }
                else -> println("Unknown field: $key")
            }
        }

        println("OK")
        return true
    }

    override fun help(): String =
        "inst_update <id> field=value ...  - Update instrument fields (name, location, status)"
}