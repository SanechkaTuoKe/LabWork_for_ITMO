package org.example.cli.services

class LoopService(
    private val commandService: CommandService
) {
    private val readerService = ReaderService()

    fun loopOfCommands() {
        while (true) {
            print("> ")
            val input = readerService.readCommand()
            val shouldContinue = commandService.execute(input)

            if (!shouldContinue) {
                println("Bye!")
                break
            }
        }
    }
}