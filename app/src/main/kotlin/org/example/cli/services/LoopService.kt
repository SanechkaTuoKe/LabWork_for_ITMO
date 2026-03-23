package org.example.cli.services


class LoopService {
    private val readerService = ReaderService()
    private val commandService = CommandService()

    fun loopOfCommands() {
        @Suppress("ControlFlowWithEmptyBody")
        while (commandService.readCommand(readerService.readCommand())){}
    }
}