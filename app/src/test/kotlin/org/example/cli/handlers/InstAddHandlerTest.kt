package org.example.cli.handlers

import org.example.domain.InstrumentType
import org.example.service.InstrumentService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InstAddHandlerTest {

    private lateinit var instrumentService: InstrumentService
    private lateinit var handler: InstAddHandler
    private lateinit var outContent: ByteArrayOutputStream
    private val originalOut = System.out
    private val originalIn = System.`in`

    @BeforeEach
    fun setup() {
        instrumentService = InstrumentService()
        handler = InstAddHandler()
        outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))
    }

    @AfterEach
    fun tearDown() {
        System.setOut(originalOut)
        System.setIn(originalIn)
    }

    private fun provideInput(vararg inputs: String) {
        val input = inputs.joinToString("\n") + "\n"
        System.setIn(ByteArrayInputStream(input.toByteArray()))
    }

    @Test
    fun `handle - should add instrument with valid input`() {
        // Arrange
        provideInput(
            "pH Meter Mettler",
            "1",
            "INV-001",
            "Lab-2 bench"
        )

        // Act
        val result = handler.handle(
            params = emptyList(),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        assertTrue(output.contains("OK instrument_id="))

        val instrumentIdStr = output.substringAfter("OK instrument_id=").trim().split("\n")[0]
        val instrumentId = instrumentIdStr.toLongOrNull()
        assertTrue(instrumentId != null)

        val instrument = instrumentService.getById(instrumentId)
        assertTrue(instrument != null)

        // Исправлено: joinToString("") убирает пробелы
        assertEquals("pHMeterMettler", instrument.name)
        assertEquals(InstrumentType.PH_METER, instrument.type)
        assertEquals("INV-001", instrument.inventoryNumber)
        assertEquals("Lab-2bench", instrument.location)  // <- без пробела!
    }

    @Test
    fun `handle - should add instrument without inventory`() {
        // Arrange
        provideInput(
            "Balance X200",
            "2",
            "",
            "Lab-1 bench"
        )

        // Act
        val result = handler.handle(
            params = emptyList(),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        assertTrue(output.contains("OK instrument_id="))

        val instrumentIdStr = output.substringAfter("OK instrument_id=").trim().split("\n")[0]
        val instrumentId = instrumentIdStr.toLongOrNull()
        assertTrue(instrumentId != null)

        val instrument = instrumentService.getById(instrumentId)
        assertEquals("BalanceX200", instrument?.name)
        assertEquals(InstrumentType.BALANCE, instrument?.type)
        assertEquals(null, instrument?.inventoryNumber)
        assertEquals("Lab-1bench", instrument?.location)  // <- без пробела!
    }

    @Test
    fun `handle - should show error for invalid type number`() {
        // Arrange
        provideInput(
            "pH Meter",
            "99",
            "INV-001",
            "Lab-2"
        )

        // Act
        val result = handler.handle(
            params = emptyList(),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()

        // Проверяем, что ошибка содержит правильное сообщение
        assertTrue(output.contains("Error: Instrument type must be from 1 to 5"),
            "Expected error message not found. Actual output: $output")
        assertTrue(!output.contains("OK instrument_id="), "Should not create instrument")
    }

    @Test
    fun `handle - should show error for blank name`() {
        // Arrange
        provideInput(
            "",
            "1",
            "INV-001",
            "Lab-2"
        )

        // Act
        val result = handler.handle(
            params = emptyList(),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        assertTrue(output.contains("Error: Name cannot be empty"))
        assertTrue(!output.contains("OK instrument_id="))
    }

    @Test
    fun `handle - should show error for non-numeric type`() {
        // Arrange
        provideInput(
            "pH Meter",
            "abc",
            "INV-001",
            "Lab-2"
        )

        // Act
        val result = handler.handle(
            params = emptyList(),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        assertTrue(output.contains("Error: Invalid type"))
        assertTrue(!output.contains("OK instrument_id="))
    }

    @Test
    fun `handle - should show error for blank location`() {
        // Arrange
        provideInput(
            "pH Meter",
            "1",
            "INV-001",
            ""
        )

        // Act
        val result = handler.handle(
            params = emptyList(),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        assertTrue(output.contains("Error: Location cannot be empty"))
        assertTrue(!output.contains("OK instrument_id="))
    }

    @Test
    fun `help - should return correct help text`() {
        // Act
        val helpText = handler.help()

        // Assert
        assertEquals("inst_add", helpText)
    }
}