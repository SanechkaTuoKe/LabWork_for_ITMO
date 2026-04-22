package org.example.storage.saveLoad

import org.example.domain.Instrument
import org.example.domain.InstrumentStatus
import org.example.domain.InstrumentType
import java.nio.file.Path
import java.time.Instant

object InstrumentSaveLoad {
    fun create(filePath: Path): SaveLoad<Instrument, Long> {
        return ServiceSaveLoad(
            filePath,
            listOf("id", "name", "type", "inventoryNumber", "location", "status", "ownerUsername", "createdAt", "updatedAt"),
            toMap = { inst ->
                mapOf(
                    "id" to inst.id.toString(),
                    "name" to inst.name,
                    "type" to inst.type.name,
                    "inventoryNumber" to (inst.inventoryNumber ?: ""),
                    "location" to inst.location,
                    "status" to inst.status.name,
                    "ownerUsername" to inst.ownerUsername,
                    "createdAt" to inst.createdAt.toString(),
                    "updatedAt" to inst.updatedAt.toString()
                )
            },
            fromMap = fromMap@{ d ->
                try {
                    Instrument(
                        id = d["id"]?.toLong() ?: return@fromMap null,
                        name = d["name"] ?: return@fromMap null,
                        type = InstrumentType.valueOf(d["type"] ?: return@fromMap null),
                        inventoryNumber = d["inventoryNumber"]?.takeIf { it.isNotBlank() },
                        location = d["location"] ?: return@fromMap null,
                        status = InstrumentStatus.valueOf(d["status"] ?: "ACTIVE"),
                        ownerUsername = d["ownerUsername"] ?: "SYSTEM",
                        createdAt = Instant.parse(d["createdAt"] ?: return@fromMap null),
                        updatedAt = Instant.parse(d["updatedAt"] ?: return@fromMap null)
                    )
                } catch (e: Exception) { null }
            },
            extractId = { it.id }
        )
    }
}
