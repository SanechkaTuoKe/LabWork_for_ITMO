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
        Files.newBufferedWriter(filePath, StandardCharsets.UTF_8).use { writer ->
            writer.write(headers.joinToString(","))
            writer.newLine()
            entities.forEach { entity ->
                val data = toMap(entity)
                writer.write(headers.joinToString(",") { data[it] ?: "" })
                writer.newLine()
            }
        }
    }

    override fun load(): Map<ID, T> {
        if (!Files.exists(filePath)) return emptyMap()
        val lines = Files.readAllLines(filePath, StandardCharsets.UTF_8)
        if (lines.size < 2) return emptyMap()

        val colIndex = lines[0].split(",").map { it.trim() }
            .withIndex().associate { it.value to it.index }

        return lines.drop(1)
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                val parts = line.split(",")
                val rowData = headers.associateWith { h ->
                    val idx = colIndex[h] ?: return@associateWith null
                    parts.getOrNull(idx)?.trim()
                }
                val entity = fromMap(rowData)
                entity?.let { extractId(it) to it }
            }.toMap()
    }

    override fun exists(): Boolean = Files.exists(filePath)
}