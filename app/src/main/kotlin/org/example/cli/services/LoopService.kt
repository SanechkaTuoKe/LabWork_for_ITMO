import org.example.cli.services.CommandService
import org.example.cli.services.ReaderService

class LoopService {

    private val readerService = ReaderService()
    private val commandService = CommandService()

    fun loadInitialData(path: String) {
        commandService.loadStartupData(path)
    }

    fun loopOfCommands() {
        while (true) {
            val input = readerService.readCommand()
            val shouldContinue = commandService.execute(input)

            if (!shouldContinue) {
                println("Bye!")
                break
            }
        }
    }
}