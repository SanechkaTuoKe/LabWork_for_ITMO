package org.example.storage

data class CsvRow(
    val fields: List<String>
) {
    fun get(index: Int): String = fields.getOrElse(index) { "" }
    fun size(): Int = fields.size
}