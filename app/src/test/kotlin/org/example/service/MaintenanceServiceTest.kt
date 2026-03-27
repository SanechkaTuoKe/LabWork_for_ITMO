package org.example.service

import org.example.domain.InstrumentType
import org.example.domain.MaintenanceType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MaintenanceServiceTest {

    private lateinit var instrumentService: InstrumentService
    private lateinit var maintenanceService: MaintenanceService
    private var instrumentId: Long = 0

    @BeforeEach
    fun setup() {
        instrumentService = InstrumentService()
        instrumentId = instrumentService.add(
            name = "Balance X200",
            type = InstrumentType.BALANCE,
            inventoryNumber = "INV-00088",  // исправлено: inventoryNumber вместо inventory
            location = "Lab-1 bench"
        ).id  // добавлено .id, так как add возвращает Instrument
        maintenanceService = MaintenanceService(instrumentService)
    }

    @Test
    fun addMaintenance_emptyDetails_throwsException() {
        val exception = assertThrows<IllegalArgumentException> {
            maintenanceService.add(
                instrumentId = instrumentId,
                type = MaintenanceType.SERVICE,  // исправлено: передаем enum, а не строку
                details = ""  // пустая строка вызовет исключение в MaintenanceValidator.validateDetails()
            )
        }
        assert(exception.message!!.contains("Details cannot be empty"))
    }
}