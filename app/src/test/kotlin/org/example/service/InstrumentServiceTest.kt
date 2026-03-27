package org.example.service

import org.example.domain.InstrumentType
import org.example.service.InstrumentService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class InstrumentServiceTest {

    private val instrumentService = InstrumentService()

    @Test
    fun testAddInstrument() {
        val instrument = instrumentService.add(
            name = "pH meter Mettler A1",
            type = InstrumentType.PH_METER,
            inventoryNumber = "INV-00077",
            location = "Lab-2 bench"
        )

        // получаем инструмент по ID из возвращенного объекта
        val retrievedInstrument = instrumentService.getById(instrument.id)
        assertNotNull(retrievedInstrument, "Instrument не найден")

        assertEquals("pH meter Mettler A1", retrievedInstrument.name)
        assertEquals(InstrumentType.PH_METER, retrievedInstrument.type)
        assertEquals("INV-00077", retrievedInstrument.inventoryNumber)
        assertEquals("Lab-2 bench", retrievedInstrument.location)
    }
}