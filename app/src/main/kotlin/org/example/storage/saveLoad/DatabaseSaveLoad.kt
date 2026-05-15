package org.example.storage.saveLoad

import java.sql.Connection
import java.sql.SQLException

class DatabaseSaveLoad<T, ID : Any>(
    private val connection: Connection,
    private val tableName: String,
    private val idColumn: String,
    private val columns: List<String>,
    private val toRow: (T) -> Map<String, Any?>,
    private val fromRow: (Map<String, Any?>) -> T?,
    private val extractId: (T) -> ID
) : SaveLoad<T, ID> {

    override fun save(entities: Collection<T>) {
        try {
            connection.createStatement().use { stmt ->
                stmt.executeUpdate("DELETE FROM $tableName")
            }

            if (entities.isEmpty()) return

            val placeholders = columns.joinToString(", ") { "?" }
            val sql = "INSERT INTO $tableName (${columns.joinToString(", ")}) VALUES ($placeholders)"

            connection.prepareStatement(sql).use { ps ->
                for (entity in entities) {
                    val row = toRow(entity)
                    columns.forEachIndexed { index, col ->
                        ps.setObject(index + 1, row[col])
                    }
                    ps.addBatch()
                }
                ps.executeBatch()
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error saving to table $tableName: ${e.message}", e)
        }
    }

    override fun load(): Map<ID, T> {
        val result = mutableMapOf<ID, T>()
        try {
            connection.createStatement().use { stmt ->
                stmt.executeQuery("SELECT * FROM $tableName").use { rs ->
                    val metaData = rs.metaData
                    while (rs.next()) {
                        val row = mutableMapOf<String, Any?>()
                        for (i in 1..metaData.columnCount) {
                            val raw = rs.getObject(i)
                            row[metaData.getColumnName(i).lowercase()] = convertValue(raw)
                        }
                        val entity = fromRow(row) ?: continue
                        result[extractId(entity)] = entity
                    }
                }
            }
        } catch (e: SQLException) {
            System.err.println("Error loading from table $tableName: ${e.message}")
        }
        return result
    }

    override fun exists(): Boolean {
        try {
            connection.createStatement().use { stmt ->
                stmt.executeQuery("SELECT COUNT(*) FROM $tableName").use { rs ->
                    rs.next()
                    return rs.getInt(1) > 0
                }
            }
        } catch (e: SQLException) {
            return false
        }
    }

    fun insert(entity: T): ID? {
        val columnsWithoutId = columns.filter { it != idColumn }
        val placeholders = columnsWithoutId.joinToString(", ") { "?" }
        val sql = "INSERT INTO $tableName (${columnsWithoutId.joinToString(", ")}) VALUES ($placeholders) RETURNING $idColumn"

        try {
            connection.prepareStatement(sql).use { ps ->
                val row = toRow(entity)
                columnsWithoutId.forEachIndexed { index, col ->
                    ps.setObject(index + 1, row[col])
                }
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        val generatedId = convertValue(rs.getObject(1))
                        @Suppress("UNCHECKED_CAST")
                        return generatedId as? ID
                    }
                }
            }
        } catch (e: SQLException) {
            when (e.sqlState) {
                "23505" -> throw RuntimeException("Duplicate entry: ${e.message}")
                "23503" -> throw RuntimeException("Referenced object does not exist: ${e.message}")
                else -> throw RuntimeException("Database error: ${e.message}", e)
            }
        }
        return null
    }

    fun update(id: ID, entity: T) {
        val columnsWithoutId = columns.filter { it != idColumn }
        val setClause = columnsWithoutId.joinToString(", ") { "$it = ?" }
        val sql = "UPDATE $tableName SET $setClause WHERE $idColumn = ?"

        try {
            connection.prepareStatement(sql).use { ps ->
                val row = toRow(entity)
                columnsWithoutId.forEachIndexed { index, col ->
                    ps.setObject(index + 1, row[col])
                }
                ps.setObject(columnsWithoutId.size + 1, id)
                ps.executeUpdate()
            }
        } catch (e: SQLException) {
            throw RuntimeException("Database error: ${e.message}", e)
        }
    }

    fun delete(id: ID) {
        try {
            connection.prepareStatement("DELETE FROM $tableName WHERE $idColumn = ?").use { ps ->
                ps.setObject(1, id)
                ps.executeUpdate()
            }
        } catch (e: SQLException) {
            if (e.sqlState == "23503") {
                throw RuntimeException("Cannot delete: record is referenced by other data")
            } else {
                throw RuntimeException("Database error: ${e.message}", e)
            }
        }
    }

    private fun convertValue(value: Any?): Any? {
        return when (value) {
            is java.math.BigDecimal -> value.toLong()
            is java.math.BigInteger -> value.toLong()
            is Integer -> value.toLong()
            is Short -> value.toLong()
            is Long -> value
            else -> value
        }
    }
}