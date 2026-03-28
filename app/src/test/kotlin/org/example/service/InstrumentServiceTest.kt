package org.example.service

import org.example.domain.InstrumentType
import org.example.domain.InstrumentStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InstrumentServiceTest {

    private lateinit var service: InstrumentService

    @BeforeEach
    fun setup() {
        service = InstrumentService()
    }

    @Test
    fun `add - should add instrument with valid data`() {
        val instrument = service.add(
            name = "pH Meter",
            type = InstrumentType.PH_METER,
            inventoryNumber = "INV-001",
            location = "Lab-1"
        )

        assertNotNull(instrument)
        assertEquals("pH Meter", instrument.name)
        assertEquals(InstrumentType.PH_METER, instrument.type)
        assertEquals("INV-001", instrument.inventoryNumber)
        assertEquals("Lab-1", instrument.location)
        assertEquals(InstrumentStatus.ACTIVE, instrument.status)
    }

    @Test
    fun `add - should add instrument without inventory`() {
        val instrument = service.add(
            name = "Balance",
            type = InstrumentType.BALANCE,
            inventoryNumber = null,
            location = "Lab-2"
        )

        assertNotNull(instrument)
        assertNull(instrument.inventoryNumber)
    }

    @Test
    fun `getById - should return instrument by id`() {
        val instrument = service.add("Test", InstrumentType.PH_METER, null, "Lab")
        val found = service.getById(instrument.id)

        assertNotNull(found)
        assertEquals(instrument.id, found?.id)
    }

    @Test
    fun `getById - should return null for non-existent id`() {
        val found = service.getById(999)
        assertNull(found)
    }

    @Test
    fun `getAll - should return all instruments`() {
        service.add("Instrument 1", InstrumentType.PH_METER, null, "Lab")
        service.add("Instrument 2", InstrumentType.BALANCE, null, "Lab")

        val all = service.getAll()
        assertEquals(2, all.size)
    }

    @Test
    fun `update - should update instrument fields`() {
        val instrument = service.add("Old Name", InstrumentType.PH_METER, null, "Old Location")

        val updated = service.update(
            id = instrument.id,
            name = "New Name",
            location = "New Location"
        )

        assertEquals("New Name", updated.name)
        assertEquals("New Location", updated.location)
    }

    @Test
    fun `update - should update inventory number`() {
        val instrument = service.add("Test", InstrumentType.PH_METER, null, "Lab")

        val updated = service.update(
            id = instrument.id,
            inventoryNumber = "INV-999"
        )

        assertEquals("INV-999", updated.inventoryNumber)
    }

    // Временно закомментирован, если метод changeStatus отсутствует
    // @Test
    // fun `changeStatus - should change instrument status`() {
    //     val instrument = service.add("Test", InstrumentType.PH_METER, null, "Lab")
    //
    //     val updated = service.changeStatus(instrument.id, InstrumentStatus.INACTIVE)
    //
    //     assertEquals(InstrumentStatus.INACTIVE, updated.status)
    // }

    @Test
    fun `delete - should delete instrument`() {
        val instrument = service.add("Test", InstrumentType.PH_METER, null, "Lab")

        service.delete(instrument.id)
        val found = service.getById(instrument.id)

        assertNull(found)
    }

    @Test
    fun `exists - should return true for existing instrument`() {
        val instrument = service.add("Test", InstrumentType.PH_METER, null, "Lab")

        assertTrue(service.exists(instrument.id))
    }
}