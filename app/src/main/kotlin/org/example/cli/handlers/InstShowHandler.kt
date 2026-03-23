package org.example.cli.handlers

import org.example.cli.services.ReaderService
import org.example.service.InstrumentService

class InstShowHandler : BaseHandler {
    var readerService = ReaderService()
    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        println("Id - ?")
        val id = readerService.readCommand().joinToString().toLong()
        println(instrumentService.getById(id))
        return true
    }

    override fun help(): String = "InstShow - show instruments profile"
}
