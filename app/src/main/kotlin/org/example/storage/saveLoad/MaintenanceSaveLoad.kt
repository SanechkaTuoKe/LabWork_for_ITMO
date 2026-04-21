package org.example.storage.saveLoad

import org.example.domain.Maintenance
import org.example.domain.MaintenanceType
import java.nio.file.Path
import java.time.Instant

object MaintenanceSaveLoad {

    fun create(filePath: Path): SaveLoad<Maintenance, Long> {
        return ServiceSaveLoad(
            filePath = filePath,
            headers = listOf("id", "instrumentId", "type", "details", "doneAt", "createdAt"),

            toMap = { m ->
                mapOf(
                    "id" to m.id.toString(),
                    "instrumentId" to m.instrumentId.toString(),
                    "type" to m.type.name,
                    "details" to m.details,
                    "doneAt" to m.doneAt.toString(),
                    "createdAt" to m.createdAt.toString(),
                    "ownerUsername" to m.ownerUsername
                )
            },

            fromMap = fromMap@{ data ->
                val id = data["id"]?.toLongOrNull() ?: return@fromMap null
                val instrumentId = data["instrumentId"]?.toLongOrNull() ?: return@fromMap null
                val type = MaintenanceType.valueOf(data["type"] ?: "REPAIR")
                val details = data["details"] ?: ""
                val doneAt = Instant.parse(data["doneAt"] ?: return@fromMap null)
                val createdAt = Instant.parse(data["createdAt"] ?: return@fromMap null)

                Maintenance(
                    id = id,
                    instrumentId = instrumentId,
                    type = type,
                    details = details,
                    doneAt = doneAt,
                    ownerUsername = ownerUsername,
                    createdAt = createdAt
                )
            },

            extractId = { it.id }
        )
    }
}