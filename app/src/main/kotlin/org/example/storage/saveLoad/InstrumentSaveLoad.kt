package org.example.storage.saveLoad

import org.example.domain.Instrument
import org.example.domain.InstrumentStatus
import org.example.domain.InstrumentType
import java.sql.Connection
import java.sql.Timestamp
import java.time.Instant

object InstrumentSaveLoad {
    fun create(connection: Connection): DatabaseSaveLoad<Instrument, Long> {
        return DatabaseSaveLoad(
            connection = connection,
            tableName = "instruments",
            idColumn = "id",
            columns = listOf(
                "id", "name", "type", "inventory_number", "location",
                "status", "owner_username", "created_at", "updated_at"
            ),
            toRow = { inst ->
                mapOf(
                    "id" to inst.id,
                    "name" to inst.name,
                    "type" to inst.type.name,
                    "inventory_number" to (inst.inventoryNumber ?: ""),
                    "location" to inst.location,
                    "status" to inst.status.name,
                    "owner_username" to (inst.ownerUsername ?: ""),
                    "created_at" to Timestamp.from(inst.createdAt),
                    "updated_at" to Timestamp.from(inst.updatedAt)
                )
            },
            fromRow = { row ->
                try {
                    Instrument(
                        id = (row["id"] as? Number)?.toLong() ?: return@DatabaseSaveLoad null,
                        name = row["name"] as? String ?: return@DatabaseSaveLoad null,
                        type = InstrumentType.valueOf(
                            row["type"] as? String ?: return@DatabaseSaveLoad null
                        ),
                        inventoryNumber = (row["inventory_number"] as? String)?.takeIf { it.isNotBlank() },
                        location = row["location"] as? String ?: return@DatabaseSaveLoad null,
                        status = InstrumentStatus.valueOf(
                            row["status"] as? String ?: "ACTIVE"
                        ),
                        ownerUsername = (row["owner_username"] as? String)?.takeIf { it.isNotBlank() },
                        createdAt = (row["created_at"] as? Timestamp)?.toInstant() ?: Instant.now(),
                        updatedAt = (row["updated_at"] as? Timestamp)?.toInstant() ?: Instant.now()
                    )
                } catch (e: Exception) {
                    null
                }
            },
            extractId = { it.id }
        )
    }
}