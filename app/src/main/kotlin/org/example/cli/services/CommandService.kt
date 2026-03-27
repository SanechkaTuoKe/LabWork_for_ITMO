package org.example.cli.services

import CalAddHandler
import org.example.cli.handlers.*
import org.example.service.InstrumentService

class CommandService() {
    private val commandList = mapOf<String, BaseHandler>(
        "exit" to ExitHandler(),
        "help" to HelpHandler(),
        "InstAdd" to InstAddHandler(),
        "InstList" to InstListHandler(),
        "InstShow" to InstShowHandler(),
        "CalAdd" to CalAddHandler(),
        "InstUpdate" to InstUpdateHandler(),
        "CalList" to CalListHandler(),
        "MaintAdd" to MaintAddHandler(),
        "MaintList" to MaintListHandler(),
        "InstDue" to InstDueHandler()
    )
    private val instrumentService = InstrumentService()

    private val commands = mapOf<String, BaseHandler>(
        "inst_add" to InstAddHandler(),
        "inst_list" to InstListHandler(),
        "inst_show" to InstShowHandler(),
        "inst_update" to InstUpdateHandler(),
        "inst_due" to InstDueHandler(),
        "cal_add" to CalAddHandler(),
        "cal_list" to CalListHandler(),
        "maint_add" to MaintAddHandler(),
        "maint_list" to MaintListHandler(),
        "help" to HelpHandler(),
        "exit" to ExitHandler()
    )

    fun execute(input: List<String>): Boolean {
        val cmd = input.getOrNull(0) ?: return true
        val handler = commands[cmd] ?: run {
            println("Error: command not found")
            return true
        }

        return try {
            handler.handle(input.drop(1), instrumentService, commands.values)
        } catch (e: Exception) {
            ErrorHandler.handle(e)
            true
        }
    }
}