package org.example.storage.saveLoad

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class ServiceSaveLoad<T, ID : Any>(
    private val filePath: Path,
    private val headers: List<String>,
    private val toMap: (T) -> Map<String, String>,
    private val fromMap: (Map<String, String>) -> T?,
    private val extractId: (T) -> ID
) : SaveLoad<T, ID> {

    override fun save(entities: Collection<T>) {
        filePath.parent?.let { Files.createDirectories(it) }
        Files.newBufferedWriter(filePath, StandardCharsets.UTF_8).use { writer ->
            writer.write(headers.joinToString(","))
            writer.newLine()
            entities.forEach { entity ->
                val data = toMap(entity)
                writer.write(headers.joinToString(",") { key ->
                    val value = data[key] ?: ""
                    if (value.contains(',') || value.contains('"') || value.contains('\n')) {
                        "\"${value.replace("\"", "\"\"")}\""
                    } else value
                })
                writer.newLine()
            }
        }
    }

    override fun load(): Map<ID, T> {
        if (!Files.exists(filePath)) return emptyMap()
        val lines = Files.readAllLines(filePath, StandardCharsets.UTF_8)
        if (lines.size < 2) return emptyMap()

        val colIndex = parseCsvLine(lines[0]).map { it.trim() }
            .withIndex().associate { it.value to it.index }

        return lines.drop(1)
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                val parts = parseCsvLine(line)
                val rowData = headers.associateWith { h ->
                    val idx = colIndex[h] ?: return@associateWith null
                    parts.getOrNull(idx)?.trim()
                }
                val entity = fromMap(rowData as Map<String, String>)
                entity?.let { extractId(it) to it }
            }.toMap()
    }

    override fun exists(): Boolean = Files.exists(filePath)

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var inQuotes = false
        val current = StringBuilder()
        for (ch in line) {
            when {
                ch == '"' -> inQuotes = !inQuotes
                ch == ',' && !inQuotes -> { result.add(current.toString()); current.clear() }
                else -> current.append(ch)
            }
        }
        result.add(current.toString())
        return result
    }
}
