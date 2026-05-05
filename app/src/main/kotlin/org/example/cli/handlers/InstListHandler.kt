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

        val type = Param.paramValue(params, "type")?.let {
            try { InstrumentType.valueOf(it.uppercase()) }
            catch (e: Exception) {
                println("Invalid type")
                return true
            }
        }

        val status = Param.paramValue(params, "status")?.let {
            try { InstrumentStatus.valueOf(it.uppercase()) }
            catch (e: Exception) {
                println("Invalid status")
                return true
            }
        }

        val list = instrumentService.getAll().filter {
            (type == null || it.type == type) &&
                    (status == null || it.status == status)
        }

        println(list.joinToString(",\n"))
        return true
    }

    override fun help(): String =
        "inst_list [--type TYPE] [--status ACTIVE|OUT_OF_SERVICE]"
}