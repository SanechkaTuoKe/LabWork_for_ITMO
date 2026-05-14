package org.example.storage

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.Properties

object DatabaseConfig {
    private var connection: Connection? = null

    fun getConnection(): Connection {
        connection?.let {
            if (!it.isClosed) return it
        }

        val props = Properties()
        try {
            val stream = this::class.java.classLoader.getResourceAsStream("database.properties")
            if (stream != null) {
                props.load(stream)
            }
        } catch (e: Exception) {
            System.err.println("Warning: Cannot load database.properties, using environment variables")
        }

        val url = props.getProperty("db.url")
            ?: System.getenv("DB_URL")
            ?: "jdbc:postgresql://localhost:5432/putao"
        val user = props.getProperty("db.user")
            ?: System.getenv("DB_USER")
            ?: "postgres"
        val password = props.getProperty("db.password")
            ?: System.getenv("DB_PASSWORD")
            ?: "admin123"

        return try {
            connection = DriverManager.getConnection(url, user, password)
            connection!!
        } catch (e: SQLException) {
            throw RuntimeException(
                "Cannot connect to database.\n" +
                        "Check: 1) PostgreSQL is running  2) URL: $url  3) User: $user\n" +
                        "Error: ${e.message}"
            )
        }
    }

    fun closeConnection() {
        try {
            connection?.close()
        } catch (e: SQLException) {
            System.err.println("Error closing connection: ${e.message}")
        }
    }
}