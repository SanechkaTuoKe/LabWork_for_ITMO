package org.example.cli.handlers

import org.example.domain.InstrumentType
import org.example.domain.MaintenanceType
import org.example.service.InstrumentService
import org.example.service.MaintenanceService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MaintListHandlerTest {

    private lateinit var instrumentService: InstrumentService
    private lateinit var maintenanceService: MaintenanceService
    private lateinit var handler: MaintListHandler
    private lateinit var outContent: ByteArrayOutputStream
    private val originalOut = System.out

    @BeforeEach
    fun setup() {
        instrumentService = InstrumentService()
        maintenanceService = MaintenanceService(instrumentService)
        handler = MaintListHandler(maintenanceService)

        outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))
    }

    @AfterEach
    fun tearDown() {
        System.setOut(originalOut)
    }

    @Test
    fun `handle - should display maintenance records for instrument`() {
        // Arrange
        val instrument = instrumentService.add(
            name = "pH Meter",
            type = InstrumentType.PH_METER,
            inventoryNumber = "INV-001",
            location = "Lab-1"
        )

        maintenanceService.add(
            instrumentId = instrument.id,
            type = MaintenanceType.SERVICE,
            details = "Annual calibration"
        )

        maintenanceService.add(
            instrumentId = instrument.id,
            type = MaintenanceType.REPAIR,
            details = "Replaced electrode"
        )

        // Act
        val result = handler.handle(
            params = listOf(instrument.id.toString()),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        assertTrue(output.contains("ID  TYPE  DATE  DETAILS"))
        assertTrue(output.contains("SERVICE"))
        assertTrue(output.contains("REPAIR"))
        assertTrue(output.contains("Annual calibration"))
        assertTrue(output.contains("Replaced electrode"))
    }

    @Test
    fun `handle - should respect last parameter and show only N records`() {
        // Arrange
        val instrument = instrumentService.add(
            name = "Balance",
            type = InstrumentType.BALANCE,
            inventoryNumber = "INV-002",
            location = "Lab-2"
        )

        repeat(3) { index ->
            maintenanceService.add(
                instrumentId = instrument.id,
                type = MaintenanceType.SERVICE,
                details = "Service record $index"
            )
        }

        // Act
        val result = handler.handle(
            params = listOf(instrument.id.toString(), "--last", "2"),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        val serviceRecordCount = output.lines().count { it.contains("Service record") }
        assertEquals(2, serviceRecordCount, "Should display only 2 records")
    }

    @Test
    fun `handle - should display message when no maintenance records`() {
        // Arrange
        val instrument = instrumentService.add(
            name = "Spectrophotometer",
            type = InstrumentType.SPECTROPHOTOMETER,
            inventoryNumber = "INV-003",
            location = "Lab-3"
        )

        // Act
        val result = handler.handle(
            params = listOf(instrument.id.toString()),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        assertTrue(output.contains("No maintenance records"))
        assertTrue(!output.contains("ID  TYPE  DATE  DETAILS"))
    }

    @Test
    fun `handle - should handle invalid instrument id and show error`() {
        // Act
        val result = handler.handle(
            params = listOf("invalid_id"),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        assertTrue(output.contains("Invalid instrument id"))
    }

    @Test
    fun `handle - should handle missing instrument id`() {
        // Act
        val result = handler.handle(
            params = emptyList(),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        assertTrue(output.contains("Invalid instrument id"))
    }

    @Test
    fun `handle - should handle non-existent instrument`() {
        // Act
        val result = handler.handle(
            params = listOf("99999"),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        assertTrue(output.contains("Instrument with id=99999 not found"))
    }

    @Test
    fun `handle - should handle invalid last parameter gracefully`() {
        // Arrange
        val instrument = instrumentService.add(
            name = "Test Instrument",
            type = InstrumentType.PH_METER,
            inventoryNumber = "INV-004",
            location = "Lab-4"
        )

        maintenanceService.add(
            instrumentId = instrument.id,
            type = MaintenanceType.SERVICE,
            details = "First service"
        )

        maintenanceService.add(
            instrumentId = instrument.id,
            type = MaintenanceType.REPAIR,
            details = "Repair work"
        )

        // Act
        val result = handler.handle(
            params = listOf(instrument.id.toString(), "--last", "invalid_number"),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        assertTrue(output.contains("First service"))
        assertTrue(output.contains("Repair work"))
        assertTrue(!output.contains("Invalid instrument id"))
    }

    @Test
    fun `handle - should format date correctly`() {
        // Arrange
        val instrument = instrumentService.add(
            name = "Test Instrument",
            type = InstrumentType.BALANCE,
            inventoryNumber = "INV-005",
            location = "Lab-5"
        )

        maintenanceService.add(
            instrumentId = instrument.id,
            type = MaintenanceType.SERVICE,
            details = "Test service"
        )

        // Act
        val result = handler.handle(
            params = listOf(instrument.id.toString()),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        val datePattern = Regex("\\d{4}-\\d{2}-\\d{2}")
        assertTrue(datePattern.containsMatchIn(output), "Date should be in yyyy-MM-dd format")
    }

    @Test
    fun `handle - should display records in correct order (newest first)`() {
        // Arrange
        val instrument = instrumentService.add(
            name = "Test Instrument",
            type = InstrumentType.PH_METER,
            inventoryNumber = "INV-006",
            location = "Lab-6"
        )

        maintenanceService.add(
            instrumentId = instrument.id,
            type = MaintenanceType.SERVICE,
            details = "First record"
        )

        Thread.sleep(10)

        maintenanceService.add(
            instrumentId = instrument.id,
            type = MaintenanceType.REPAIR,
            details = "Second record"
        )

        // Act
        val result = handler.handle(
            params = listOf(instrument.id.toString()),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        val lines = output.lines()
        val repairIndex = lines.indexOfFirst { it.contains("REPAIR") }
        val serviceIndex = lines.indexOfFirst { it.contains("SERVICE") }

        assertTrue(repairIndex < serviceIndex, "Newer record should appear first")
    }

    @Test
    fun `help - should return correct help text`() {
        // Act
        val helpText = handler.help()

        // Assert
        assertEquals("maint_list <instrument_id> [--last N]", helpText)
    }

    @Test
    fun `handle - should handle multiple instruments with separate records`() {
        // Arrange
        val instrument1 = instrumentService.add(
            name = "Instrument 1",
            type = InstrumentType.PH_METER,
            inventoryNumber = "INV-007",
            location = "Lab-7"
        )

        val instrument2 = instrumentService.add(
            name = "Instrument 2",
            type = InstrumentType.BALANCE,
            inventoryNumber = "INV-008",
            location = "Lab-8"
        )

        maintenanceService.add(
            instrumentId = instrument1.id,
            type = MaintenanceType.SERVICE,
            details = "Service for instrument 1"
        )

        maintenanceService.add(
            instrumentId = instrument2.id,
            type = MaintenanceType.REPAIR,
            details = "Repair for instrument 2"
        )

        // Act
        val result = handler.handle(
            params = listOf(instrument1.id.toString()),
            instrumentService = instrumentService,
            commandList = emptyList()
        )

        // Assert
        assertTrue(result)
        val output = outContent.toString()
        assertTrue(output.contains("Service for instrument 1"))
        assertTrue(!output.contains("Repair for instrument 2"))
    }
}