package org.example.storage

import java.io.File
import java.nio.file.Path

object AppData {
    val folder: Path by lazy {
        val osName = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")

        val path = when {
            osName.contains("win") -> "$userHome\\AppData\\Roaming\\PuTao"
            osName.contains("mac") -> "$userHome/Library/Application Support/PuTao"
            else -> "$userHome/.PuTao"
        }

        val dir = File(path)
        if (!dir.exists()) dir.mkdirs()
        dir.toPath()
    }

    fun resolve(fileName: String): Path = folder.resolve(fileName)
}