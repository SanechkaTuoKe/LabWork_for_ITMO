package org.example.storage.saveLoad

import org.example.domain.Calibration
import org.example.domain.CalibrationResult
import org.example.domain.CalibrationType
import java.nio.file.Path
import java.time.Instant
import org.example.storage.AppData


object CalibrationSaveLoad {
    fun create(filePath: Path): SaveLoad<Calibration, Long> {
        return ServiceSaveLoad(
            filePath,
            listOf("id", "instrumentId", "type", "result", "comment", "calibratedAt", "ownerUsername", "createdAt"),
            toMap = { cal ->
                mapOf(
                    "id" to cal.id.toString(),
                    "instrumentId" to cal.instrumentId.toString(),
                    "type" to cal.type.name,
                    "result" to cal.result.name,
                    "comment" to (cal.comment ?: ""),
                    "calibratedAt" to cal.calibratedAt.toString(),
                    "ownerUsername" to cal.ownerUsername,
                    "createdAt" to cal.createdAt.toString()
                )
            },
            fromMap = fromMap@{ d ->
                try {
                    Calibration(
                        id = d["id"]?.toLong() ?: return@fromMap null,
                        instrumentId = d["instrumentId"]?.toLong() ?: return@fromMap null,
                        type = CalibrationType.valueOf(d["type"] ?: return@fromMap null),
                        result = CalibrationResult.valueOf(d["result"] ?: return@fromMap null),
                        comment = d["comment"]?.takeIf { it.isNotBlank() } ?: "",
                        calibratedAt = Instant.parse(d["calibratedAt"] ?: return@fromMap null),
                        ownerUsername = d["ownerUsername"] ?: "SYSTEM",
                        createdAt = Instant.parse(d["createdAt"] ?: return@fromMap null)
                    )
                } catch (e: Exception) { null }
            },
            extractId = { it.id }
        )
    }
}
