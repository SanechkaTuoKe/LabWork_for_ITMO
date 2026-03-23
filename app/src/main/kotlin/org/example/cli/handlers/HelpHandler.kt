package org.example.cli.handlers

import org.example.service.InstrumentService

class HelpHandler : BaseHandler {
    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        println(commandList.joinToString("\n") {
            it.help()
        })
        return true
    }
    override fun help(): String =
        "help - list of commands"
}