package org.example.cli.services

import org.example.cli.handlers.*
import org.example.service.*

class CommandService {

    private val instrumentService = InstrumentService()
    private val calibrationService = CalibrationService(instrumentService)
    private val maintenanceService = MaintenanceService(instrumentService)

    private val commands: Map<String, BaseHandler> = mapOf(

        "inst_add" to InstAddHandler(),
        "inst_list" to InstListHandler(),
        "inst_show" to InstShowHandler(),
        "inst_update" to InstUpdateHandler(),
        "inst_due" to InstDueHandler(calibrationService),

        "cal_add" to CalAddHandler(calibrationService),
        "cal_list" to CalListHandler(calibrationService),
        "cal_show" to CalShowHandler(calibrationService),

        "maint_add" to MaintAddHandler(maintenanceService),
        "maint_list" to MaintListHandler(maintenanceService),

        "help" to HelpHandler(),
        "exit" to ExitHandler()
    )

    fun execute(input: List<String>): Boolean {
        if (input.isEmpty()) return true

        val cmd = input[0]
        val params = input.drop(1)

        val handler = commands[cmd]
        if (handler == null) {
            println("Error: command not found")
            return true
        }

        return try {
            handler.handle(params, instrumentService, commands.values)
        } catch (e: Exception) {
            ErrorHandler.handle(e)
            true
        }
    }
}