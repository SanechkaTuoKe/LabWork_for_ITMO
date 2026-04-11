package org.example.storage

data class CsvRow(
    val fields: List<String>
) {
    fun get(index: Int): String {
        if (index >= 0 && index < fields.size) {
            return fields[index]
        } else {
            return ""
        }
    }
    val size: Int get() = fields.size
}