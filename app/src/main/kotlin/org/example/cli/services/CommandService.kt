package org.example.cli.services

import org.example.cli.handlers.*
import org.example.service.*
import org.example.storage.*

class CommandService {

    internal val instrumentService = InstrumentService()
    private val calibrationService = CalibrationService(instrumentService)
    private val maintenanceService = MaintenanceService(instrumentService)
    private val storageService = StorageService(FileStorage(), FileValidator())

    init {
        storageService.attachCollections(
            instrumentService.getAllInstruments(),
            calibrationService.getAllCalibrations(),
            maintenanceService.getAllMaintenances()
        )
    }

    fun loadStartupData(path: String) {
        storageService.load(path)
    }

    private val commands: Map<String, BaseHandler> = mapOf(
        "inst_add" to InstAddHandler(),
        "inst_list" to InstListHandler(),
        "inst_show" to InstShowHandler(calibrationService),
        "inst_update" to InstUpdateHandler(),
        "inst_due" to InstDueHandler(calibrationService),

        "cal_add" to CalAddHandler(calibrationService),
        "cal_list" to CalListHandler(calibrationService),
        "cal_show" to CalShowHandler(calibrationService),

        "maint_add" to MaintAddHandler(maintenanceService),
        "maint_list" to MaintListHandler(maintenanceService),

        "save" to SaveHandler(storageService),
        "load" to LoadHandler(storageService),

        "help" to HelpHandler(),
        "exit" to ExitHandler()
    )

    fun execute(input: List<String>): Boolean {
        if (input.isEmpty()) return true
        val cmd = input[0]
        val params = input.drop(1)
        val handler = commands[cmd]

        return if (handler != null) {
            try {
                handler.handle(params, instrumentService, commands.values)
            } catch (e: Exception) {
                ErrorHandler.handle(e)
                true
            }
        } else {
            println("Command not found")
            true
        }
    }

    fun loopOfCommands() {
    }
}
