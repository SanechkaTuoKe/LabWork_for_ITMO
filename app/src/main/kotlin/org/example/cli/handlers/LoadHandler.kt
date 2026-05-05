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
        if (params.size != 1) { println("Usage: load <directory>"); return true }
        return try {
            storageService.load(params[0])
            println("✔ Loaded from: ${params[0]}")
            true
        } catch (e: Exception) {
            println("Load error: ${e.message}")
            true
        }
    }
    override fun help() = "load <directory> - load data from files"
}
