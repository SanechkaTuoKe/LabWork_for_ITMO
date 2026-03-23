package org.example.cli.services

import CalAddHandler
import org.example.cli.handlers.*
import org.example.service.InstrumentService

class CommandService() {
    private val commandList = mapOf<String, BaseHandler>(
        "exit" to ExitHandler(),
        "help" to HelpHandler(),
        "inst_add" to InstAddHandler(),
        "InstList" to InstListHandler(),
        "InstShow" to InstShowHandler(),
        "CalAdd" to CalAddHandler(),
        "inst_update" to InstUpdateHandler(),
        "cal_list" to CalListHandler(),
        "maint_add" to MaintAddHandler(),
        "maint_list" to MaintListHandler(),
        "inst_due" to InstDueHandler()
    )
    private val instrumentService = InstrumentService()
    fun readCommand( commands : List<String>) : Boolean {
        val command = commands.getOrNull(0)
        if(command != null) {
            val handler = commandList[command]
            if(handler != null) {
                return handler.handle(commands.subList(1, commands.size),
                    instrumentService,
                    commandList.values)
            }
        }
        println("Command not found")
        return true
    }
}