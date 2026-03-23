package org.example.cli.handlers

import org.example.cli.util.Param
import org.example.domain.InstrumentStatus
import org.example.domain.InstrumentType
import org.example.service.InstrumentService

class InstListHandler : BaseHandler {
    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        val typeAsString = Param.paramValue(params, "type")
        var type : InstrumentType? = null
        var status : InstrumentStatus? = null
        if(typeAsString != null) {
            try {
                type = InstrumentType.valueOf(typeAsString)
            }
            catch (@Suppress("unused") e : Exception) {
                println("Type not found")
                return true
            }
        }
        val statusAsString = Param.paramValue(params, "status")
        if (statusAsString != null) {
            try {
                status = InstrumentStatus.valueOf(statusAsString)
            }
            catch (@Suppress("unused") e : Exception) {
                println("Status not found")
                return true
            }
        }
        println(instrumentService.list(type, status).joinToString(",\n"))
        return true
    }

    override fun help(): String =
        "instlist  [--type TYPE] [--status ACTIVE|OUT_OF_SERVICE]  - list of instruments"
}