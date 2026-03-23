import org.example.cli.handlers.BaseHandler
import org.example.cli.services.ReaderService
import org.example.service.CalibrationService
import org.example.service.InstrumentService

class CalAddHandler: BaseHandler {
    private val readerService = ReaderService()

    override fun handle(

        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        val calibrationService = CalibrationService(instrumentService)
        val instrumentId = params.getOrNull(0)?.toLongOrNull()
        if (instrumentId == null) {
            println("Invalid instrument id")
            return true
        }

        println("Calibration type:")
        val typeInput = readerService.readCommand().joinToString("").uppercase()

        val type = when (typeInput) {
            "ONE_POINT" -> 1
            "TWO_POINT" -> 2
            else -> {
                println("Wrong calibration type")
                return true
            }
        }

        println("Result (OK/FAIL):")
        val resultInput = readerService
            .readCommand()
            .joinToString("")
            .trim()
            .uppercase()

        val result = when (resultInput) {
            "OK" -> "OK"
            "FAIL" -> "FAIL"
            else -> {
                println("Wrong calibration result")
                return true
            }
        }

        println("Comments (optional):")
        val comment = readerService.readCommand().joinToString(" ")

        val calibration = calibrationService.add(
            instrumentId = instrumentId,
            type = type,
            result = result,
            comment = comment
        )

        println("OK calibration_id=${calibration.id}")

        return true
    }

    override fun help(): String {
        return "CalAdd <instrument_id> - Add Calibration"
    }
}