package org.example.cli.handlers

import org.example.service.InstrumentService

interface BaseHandler {
    fun handle(params: List<String>,
               instrumentService: InstrumentService,
               commandList: Collection<BaseHandler>): Boolean
    fun help(): String
}