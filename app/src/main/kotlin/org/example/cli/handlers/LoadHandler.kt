package org.example.cli.handlers

import org.example.service.InstrumentService
import org.example.storage.StorageService

class LoadHandler(
    private val storageService: StorageService
) : BaseHandler {

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        allHandlers: Collection<BaseHandler>
    ): Boolean {

        if (params.size != 1) {
            println("Usage: load <directory>")
            return true
        }

        val path = params[0]

        try {
            storageService.load(path)
            println("✔ Data successfully loaded from: $path")
        } catch (e: IllegalArgumentException) {
            println("Load error:")
            println(e.message)
        } catch (e: Exception) {
            println("Unexpected error during load: ${e.message}")
        }

        return true
    }

    override fun help(): String {
        return "load <directory> - load data from files (replaces current data)"
    }
}