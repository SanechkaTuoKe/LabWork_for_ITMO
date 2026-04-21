package org.example.storage.saveLoad

import org.example.domain.Calibration
import org.example.domain.CalibrationResult
import org.example.domain.CalibrationType
import java.nio.file.Path
import java.time.Instant

object CalibrationSaveLoad {
    fun create(filePath: Path): SaveLoad<Calibration, Long> {
        return ServiceSaveLoad(
            filePath,
            listOf("id", "instrumentId", "type", "result", "date", "createdAt"),
            toMap = { cal ->
                mapOf(
                    "id" to cal.id.toString(),
                    "instrumentId" to cal.instrumentId.toString(),
                    "type" to cal.type.name,
                    "result" to cal.result.name,
                    "date" to cal.date.toString(),
                    "createdAt" to cal.createdAt.toString()
                )
            },
            fromMap = { d ->
                try {
                    Calibration(
                        id = d["id"]?.toLong() ?: return@ServiceSaveLoad null,
                        instrumentId = d["instrumentId"]?.toLong() ?: return@ServiceSaveLoad null,
                        type = CalibrationType.valueOf(d["type"] ?: "PRELIMINARY"),
                        result = CalibrationResult.valueOf(d["result"] ?: "PASSED"),
                        date = Instant.parse(d["date"] ?: return@ServiceSaveLoad null),
                        createdAt = Instant.parse(d["createdAt"] ?: return@ServiceSaveLoad null)
                    )
                } catch (e: Exception) { null }
            },
            extractId = { it.id }
        )
    }
}