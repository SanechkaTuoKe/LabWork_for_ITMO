package org.example.service

import org.example.domain.Instrument
import org.example.domain.InstrumentStatus
import org.example.domain.InstrumentType
import org.example.validation.InstrumentValidator
import java.time.Instant
import java.util.TreeMap
import org.example.storage.saveLoad.InstrumentSaveLoad
import java.sql.Connection

class InstrumentService(private val connection: Connection? = null)  {

    private val instruments = TreeMap<Long, Instrument>()
    private var nextId = 1L
    private val dbStorage = connection?.let { InstrumentSaveLoad.create(it) }

    init {
        connection?.let {
            loadFromDatabase()
        }
    }

    fun loadFromDatabase() {
        dbStorage?.let { storage ->
            val loaded = storage.load()
            instruments.clear()
            instruments.putAll(loaded)
            nextId = if (loaded.isEmpty()) 1L else loaded.keys.max() + 1L
        }
    }

    fun add(
        name: String,
        type: InstrumentType,
        inventoryNumber: String?,
        location: String,
        ownerUsername: String?
    ): Instrument {
        InstrumentValidator.validateName(name)
        InstrumentValidator.validateLocation(location)
        val validatedInventory = inventoryNumber
            ?.takeIf { it.isNotBlank() }
            ?.also { InstrumentValidator.validateInventoryNumber(it) }

        val now = Instant.now()

        if (dbStorage != null) {
            val tempInstrument = Instrument(
                id = 0,
                name = name,
                type = type,
                inventoryNumber = validatedInventory,
                location = location,
                status = InstrumentStatus.ACTIVE,
                ownerUsername = ownerUsername,
                createdAt = now,
                updatedAt = now
            )
            val generatedId = dbStorage!!.insert(tempInstrument)
            if (generatedId != null) {
                val instrument = tempInstrument.copy(id = generatedId)
                instruments[generatedId] = instrument
                return instrument
            }
        }

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
        instruments[id] ?: throw NoSuchElementException("Instrument with id=$id not found")

    fun getAll(): List<Instrument> = instruments.values.toList()

    fun update(
        id: Long,
        name: String? = null,
        location: String? = null,
        inventoryNumber: String? = null
    ): Instrument {
        val instrument = getByIdOrThrow(id)
        if (name != null) {
            InstrumentValidator.validateName(name)
            instrument.name = name
        }
        if (location != null) {
            InstrumentValidator.validateLocation(location)
            instrument.location = location
        }
        if (inventoryNumber != null) {
            instrument.inventoryNumber = inventoryNumber
                .takeIf { it.isNotBlank() }
                ?.also { InstrumentValidator.validateInventoryNumber(it) }
        }
        instrument.updatedAt = Instant.now()
        dbStorage?.update(id, instrument)
        return instrument
    }

    fun changeStatus(id: Long, status: InstrumentStatus): Instrument {
        val instrument = getByIdOrThrow(id)
        instrument.status = status
        instrument.updatedAt = Instant.now()
        return instrument
    }

    fun delete(id: Long) {
        dbStorage?.delete(id)
        if (instruments.remove(id) == null) {
            throw NoSuchElementException("Instrument with id=$id not found")
        }
    }

    fun exists(id: Long): Boolean = instruments.containsKey(id)

    fun loadAll(loaded: Map<Long, Instrument>) {
        instruments.clear()
        instruments.putAll(loaded)
        nextId = if (loaded.isEmpty()) 1L else loaded.keys.max() + 1L
    }

    fun addExisting(instrument: Instrument) {
        instruments[instrument.id] = instrument
        if (instrument.id >= nextId) {
            nextId = instrument.id + 1
        }
    }

    fun clearAll() {
        instruments.clear()
        nextId = 1L
    }

    internal fun getAllInstruments(): TreeMap<Long, Instrument> = instruments
}
