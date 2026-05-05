package org.example.cli.handlers

import org.example.service.InstrumentService

class ExitHandler : BaseHandler {
    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        println("Exiting...")
        return false
    }

    override fun help(): String = "exit - Exit the program"
}