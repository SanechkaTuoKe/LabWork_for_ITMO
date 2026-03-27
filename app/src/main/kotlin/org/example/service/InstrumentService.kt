package org.example.service

import org.example.domain.*
import org.example.validation.InstrumentValidator
import java.time.Instant
import java.util.*

class InstrumentService {

    private val instruments = TreeMap<Long, Instrument>()
    private var nextId = 1L

    fun add(
        name: String,
        type: InstrumentType,
        inventoryNumber: String?,
        location: String,
        ownerUsername: String = "SYSTEM"
    ): Instrument {

        InstrumentValidator.validateName(name)
        InstrumentValidator.validateLocation(location)

        val validatedInventory = inventoryNumber
            ?.takeIf { it.isNotBlank() }
            ?.also { InstrumentValidator.validateInventoryNumber(it) }

        val now = Instant.now()

        val instrument = Instrument(
            id = nextId++,
            name = name,
            type = type,
            inventoryNumber = validatedInventory,
            location = location,
            status = InstrumentStatus.ACTIVE,
            ownerUsername = ownerUsername,
            createdAt = now,
            updatedAt = now
        )

        instruments[instrument.id] = instrument
        return instrument
    }

    fun getById(id: Long): Instrument? = instruments[id]

    fun getByIdOrThrow(id: Long): Instrument =
        instruments[id]
            ?: throw NoSuchElementException("Instrument with id=$id not found")

    fun getAll(): List<Instrument> =
        instruments.values.toList()

    fun update(
        id: Long,
        name: String? = null,
        location: String? = null,
        inventoryNumber: String? = null
    ): Instrument {

        val instrument = getByIdOrThrow(id)

        name?.let {
            InstrumentValidator.validateName(it)
            instrument.name = it
        }

        location?.let {
            InstrumentValidator.validateLocation(it)
            instrument.location = it
        }

        if (inventoryNumber != null) {
            instrument.inventoryNumber =
                inventoryNumber.takeIf { it.isNotBlank() }
                    ?.also { InstrumentValidator.validateInventoryNumber(it) }
        }

        instrument.updatedAt = Instant.now()
        return instrument
    }

    fun changeStatus(id: Long, status: InstrumentStatus): Instrument {
        val instrument = getByIdOrThrow(id)
        instrument.status = status
        instrument.updatedAt = Instant.now()
        return instrument
    }

    fun delete(id: Long) {
        if (instruments.remove(id) == null) {
            throw NoSuchElementException("Instrument with id=$id not found")
        }
    }

    fun exists(id: Long): Boolean = instruments.containsKey(id)
}