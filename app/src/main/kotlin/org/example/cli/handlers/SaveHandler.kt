package org.example.cli.handlers

import org.example.service.InstrumentService

class SaveHandler(
    private val storageService: StorageService
) : BaseHandler {

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        allHandlers: Collection<BaseHandler>
    ): Boolean {

        if (params.size != 1) {
            println("Usage: save <directory>")
            return true
        }

        val path = params[0]

        try {
            storageService.save(path)
            println("✔ Data successfully saved to: $path")
        } catch (e: Exception) {
            println("Save failed: ${e.message}")
        }

        return true
    }

    override fun help(): String {
        return "save <directory> - save all data to files"
    }
}