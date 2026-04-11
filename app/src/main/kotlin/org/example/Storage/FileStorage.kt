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
        instruments: TreeMap<Long, Instrument>, //интерфейс для загрузок - сохранений, на одну коллекцию по классу
        calibrations: TreeMap<Long, Calibration>,
        maintenances: TreeMap<Long, Maintenance>
    ) {
        val folder = Paths.get(folderPath) //рекомендуют убрать гет?
        if (!Files.exists(folder)) {Files.createDirectories(folder) }
        saveInstruments(folder.resolve("instruments.csv"), instruments)
        saveCalibrations(folder.resolve("calibrations.csv"), calibrations)
        saveMaintenances(folder.resolve("maintenances.csv"), maintenances)
    }

    private fun saveInstruments(path: Path, map: TreeMap<Long, Instrument>) {
        writeCsv(path, "id,name,type,inventory,location,status,owner,createdAt,updatedAt")
        { writer ->
            map.values.forEach { instr ->
                writer.write("${instr.id},${escape(instr.name)},${instr.type.name},${escape(instr.inventoryNumber ?: "")},${escape(instr.location)},${instr.status.name},${escape(instr.ownerUsername)},${instr.createdAt},${instr.updatedAt}\n")
            }
        }
    }

    private fun saveCalibrations(path: Path, map: TreeMap<Long, Calibration>) {
        writeCsv(path, "id,instrumentId,type,result,date") { writer ->
            map.values.forEach { cal ->
                writer.write("${cal.id},${cal.instrumentId},${cal.type.name},${cal.result.name},${cal.createdAt}\n")
            }
        }
    }

    private fun saveMaintenances(path: Path, map: TreeMap<Long, Maintenance>) {
        writeCsv(path, "id,instrumentId,type,details,date") { writer ->
            map.values.forEach { maint ->
                writer.write("${maint.id},${maint.instrumentId},${maint.type.name},${escape(maint.details)},${maint.createdAt}\n")
            }
        }
    }

    private fun writeCsv(path: Path, header: String, writeData: (java.io.BufferedWriter) -> Unit) {
        Files.newBufferedWriter(path, StandardCharsets.UTF_8).use { writer ->
            writer.write("$header\n")
            writeData(writer)
        }
    }

    fun loadAll(folderPath: String) = Triple(
        loadFile("$folderPath/instruments.csv"),
        loadFile("$folderPath/calibrations.csv"),
        loadFile("$folderPath/maintenances.csv")
    )

    private fun loadFile(filePath: String): List<CsvRow> {
        val path = Paths.get(filePath)
        if (!Files.exists(path)) return emptyList()

        return Files.readAllLines(path, StandardCharsets.UTF_8)
            .drop(1)  // заголовок
            .filter { it.isNotBlank() }
            .map { CsvRow(parseCsvLine(it)) }
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false

        line.forEach { ch ->
            when {
                ch == '"' -> inQuotes = !inQuotes
                ch == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current.clear()
                }
                else -> current.append(ch)
            }
        }
        result.add(current.toString())
        return result
    }

    private fun escape(text: String) =
        if (text.contains(",") || text.contains("\"")) "\"${text.replace("\"", "\"\"")}\"" else text
}