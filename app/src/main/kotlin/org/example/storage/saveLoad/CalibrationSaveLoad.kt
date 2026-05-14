package org.example.storage.saveLoad

import org.example.domain.Calibration
import org.example.domain.CalibrationResult
import org.example.domain.CalibrationType
import java.sql.Connection
import java.sql.Timestamp
import java.time.Instant

object CalibrationSaveLoad {
    fun create(connection: Connection): DatabaseSaveLoad<Calibration, Long> {
        return DatabaseSaveLoad(
            connection = connection,
            tableName = "calibrations",
            idColumn = "id",
            columns = listOf(
                "id", "instrument_id", "type", "result", "comment",
                "calibrated_at", "owner_username", "created_at"
            ),
            toRow = { cal ->
                mapOf(
                    "id" to cal.id,
                    "instrument_id" to cal.instrumentId,
                    "type" to cal.type.name,
                    "result" to cal.result.name,
                    "comment" to cal.comment,
                    "calibrated_at" to Timestamp.from(cal.calibratedAt),
                    "owner_username" to cal.ownerUsername,
                    "created_at" to Timestamp.from(cal.createdAt)
                )
            },
            fromRow = { row ->
                try {
                    Calibration(
                        id = (row["id"] as? Number)?.toLong() ?: return@DatabaseSaveLoad null,
                        instrumentId = (row["instrument_id"] as? Number)?.toLong()
                            ?: return@DatabaseSaveLoad null,
                        type = CalibrationType.valueOf(
                            row["type"] as? String ?: return@DatabaseSaveLoad null
                        ),
                        result = CalibrationResult.valueOf(
                            row["result"] as? String ?: return@DatabaseSaveLoad null
                        ),
                        comment = row["comment"] as? String ?: "",
                        calibratedAt = (row["calibrated_at"] as? Timestamp)?.toInstant()
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