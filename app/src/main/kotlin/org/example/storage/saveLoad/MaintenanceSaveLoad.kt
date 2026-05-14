package org.example.storage.saveLoad

import org.example.domain.Maintenance
import org.example.domain.MaintenanceType
import java.sql.Connection
import java.sql.Timestamp
import java.time.Instant

object MaintenanceSaveLoad {
    fun create(connection: Connection): DatabaseSaveLoad<Maintenance, Long> {
        return DatabaseSaveLoad(
            connection = connection,
            tableName = "maintenances",
            idColumn = "id",
            columns = listOf(
                "id", "instrument_id", "type", "details", "done_at",
                "owner_username", "created_at"
            ),
            toRow = { m ->
                mapOf(
                    "id" to m.id,
                    "instrument_id" to m.instrumentId,
                    "type" to m.type.name,
                    "details" to m.details,
                    "done_at" to Timestamp.from(m.doneAt),
                    "owner_username" to m.ownerUsername,
                    "created_at" to Timestamp.from(m.createdAt)
                )
            },
            fromRow = { row ->
                try {
                    Maintenance(
                        id = (row["id"] as? Number)?.toLong() ?: return@DatabaseSaveLoad null,
                        instrumentId = (row["instrument_id"] as? Number)?.toLong()
                            ?: return@DatabaseSaveLoad null,
                        type = MaintenanceType.valueOf(
                            row["type"] as? String ?: "REPAIR"
                        ),
                        details = row["details"] as? String ?: "",
                        doneAt = (row["done_at"] as? Timestamp)?.toInstant()
                            ?: return@DatabaseSaveLoad null,
                        ownerUsername = row["owner_username"] as? String ?: "SYSTEM",
                        createdAt = (row["created_at"] as? Timestamp)?.toInstant() ?: Instant.now()
                    )
                } catch (e: Exception) {
                    null
                }
            },
            extractId = { it.id }
        )
    }
}