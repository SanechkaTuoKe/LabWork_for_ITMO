package org.example.cli.handlers

import org.example.service.InstrumentService
import org.example.storage.StorageService

class SaveHandler(
    private val storageService: StorageService
) : BaseHandler {
    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        allHandlers: Collection<BaseHandler>
    ): Boolean {
        if (params.size != 1) { println("Usage: save <directory>"); return true }
        return try {
            storageService.save(params[0])
            println("✔ Saved to: ${params[0]}")
            true
        } catch (e: Exception) {
            println("Save failed: ${e.message}")
            true
        }
    }
    override fun help() = "save <directory> - save all data to files"
}
