package org.example.service

import org.example.domain.InstrumentType
import org.example.domain.MaintenanceType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MaintenanceServiceTest {

    private lateinit var instrumentService: InstrumentService
    private lateinit var maintenanceService: MaintenanceService
    private var instrumentId: Long = 0

    @BeforeEach
    fun setup() {
        instrumentService = InstrumentService()
        val instrument = instrumentService.add(
            name = "Balance",
            type = InstrumentType.BALANCE,
            inventoryNumber = "INV-001",
            location = "Lab-1"
        )
        instrumentId = instrument.id
        maintenanceService = MaintenanceService(instrumentService)
    }

    @Test
    fun `add - should add maintenance record`() {
        val maintenance = maintenanceService.add(
            instrumentId = instrumentId,
            type = MaintenanceType.SERVICE,
            details = "Regular service"
        )

        assertNotNull(maintenance)
        assertEquals(instrumentId, maintenance.instrumentId)
        assertEquals(MaintenanceType.SERVICE, maintenance.type)
        assertEquals("Regular service", maintenance.details)
    }

    @Test
    fun `add - should throw exception for empty details`() {
        assertThrows<IllegalArgumentException> {
            maintenanceService.add(instrumentId, MaintenanceType.SERVICE, "")
        }
    }

    @Test
    fun `add - should throw exception for blank details`() {
        assertThrows<IllegalArgumentException> {
            maintenanceService.add(instrumentId, MaintenanceType.SERVICE, "   ")
        }
    }

    @Test
    fun `listByInstrument - should return maintenance records`() {
        maintenanceService.add(instrumentId, MaintenanceType.SERVICE, "Service 1")
        maintenanceService.add(instrumentId, MaintenanceType.REPAIR, "Repair 1")

        val list = maintenanceService.listByInstrument(instrumentId)

        assertEquals(2, list.size)
    }

    @Test
    fun `listByInstrument - should return last N records`() {
        maintenanceService.add(instrumentId, MaintenanceType.SERVICE, "Service 1")
        maintenanceService.add(instrumentId, MaintenanceType.REPAIR, "Repair 1")
        maintenanceService.add(instrumentId, MaintenanceType.SERVICE, "Service 2")

        val last2 = maintenanceService.listByInstrument(instrumentId, 2)

        assertEquals(2, last2.size)
    }
}