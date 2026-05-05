package org.example.storage

import org.example.domain.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.TreeMap

class FileStorage {

    fun saveAll(
        folderPath: String,
        instruments: TreeMap<Long, Instrument>,
        calibrations: TreeMap<Long, Calibration>,
        maintenances: TreeMap<Long, Maintenance>
    ) {
        val folder = Paths.get(folderPath)
        if (!Files.exists(folder)) Files.createDirectories(folder)

        saveInstruments(folder.resolve("instruments.csv"), instruments)
        saveCalibrations(folder.resolve("calibrations.csv"), calibrations)
        saveMaintenances(folder.resolve("maintenances.csv"), maintenances)
    }

    private fun saveInstruments(path: Path, instruments: TreeMap<Long, Instrument>) {
        Files.newBufferedWriter(path, StandardCharsets.UTF_8).use { writer ->
            writer.write("id,name,type,inventory,location,status,owner,createdAt,updatedAt")
            writer.newLine()

            for (instr in instruments.values) {
                writer.write(
                    listOf(
                        instr.id,
                        escape(instr.name),
                        instr.type.name,
                        escape(instr.inventoryNumber ?: ""),
                        escape(instr.location),
                        instr.status.name,
                        escape(instr.ownerUsername),
                        instr.createdAt,
                        instr.updatedAt
                    ).joinToString(",")
                )
                writer.newLine()
            }
        }
    }

    private fun saveCalibrations(path: Path, calibrations: TreeMap<Long, Calibration>) {
        Files.newBufferedWriter(path, StandardCharsets.UTF_8).use { writer ->
            writer.write("id,instrumentId,type,result,date")
            writer.newLine()

            for (cal in calibrations.values) {
                writer.write(
                    listOf(
                        cal.id,
                        cal.instrumentId,
                        cal.type.name,
                        cal.result.name,
                        cal.createdAt
                    ).joinToString(",")
                )
                writer.newLine()
            }
        }
    }

    private fun saveMaintenances(path: Path, maintenances: TreeMap<Long, Maintenance>) {
        Files.newBufferedWriter(path, StandardCharsets.UTF_8).use { writer ->
            writer.write("id,instrumentId,type,details,date")
            writer.newLine()

            for (maint in maintenances.values) {
                writer.write(
                    listOf(
                        maint.id,
                        maint.instrumentId,
                        maint.type.name,
                        escape(maint.details),
                        maint.createdAt
                    ).joinToString(",")
                )
                writer.newLine()
            }
        }
    }

    fun loadAll(folderPath: String): Triple<List<CsvRow>, List<CsvRow>, List<CsvRow>> {
        val folder = Paths.get(folderPath)
        if (!Files.exists(folder)) {
            throw IllegalArgumentException("Directory not found: $folderPath")
        }

        return Triple(
            loadFile(folder.resolve("instruments.csv")),
            loadFile(folder.resolve("calibrations.csv")),
            loadFile(folder.resolve("maintenances.csv"))
        )
    }

    private fun loadFile(path: Path): List<CsvRow> {
        if (!Files.exists(path)) return emptyList()

        val rows = mutableListOf<CsvRow>()
        val reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)
        try {
            reader.readLine()
            var line = reader.readLine()
            while (line != null) {
                if (line.trim().isNotEmpty()) {
                    rows.add(CsvRow(parseCsvLine(line)))
                }
                line = reader.readLine()
            }
        } finally {
            reader.close()
        }
        return rows
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false

        var i = 0
        while (i < line.length) {
            val ch = line[i]

            if (ch == '"') {
                inQuotes = !inQuotes
            } else if (ch == ',' && !inQuotes) {
                result.add(current.toString())
                current.setLength(0)
            } else {
                current.append(ch)
            }
            i++
        }

        result.add(current.toString())
        return result
    }

    private fun escape(text: String): String {
        return if (text.contains(",") || text.contains("\"")) {
            "\"" + text.replace("\"", "\"\"") + "\""
        } else text
    }
}