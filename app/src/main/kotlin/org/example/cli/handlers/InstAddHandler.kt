package org.example.cli.handlers

import org.example.cli.services.ReaderService
import org.example.domain.InstrumentType
import org.example.service.InstrumentService
import javax.sound.midi.Instrument

class InstAddHandler: BaseHandler {
    private val readerService = ReaderService()

    override fun handle(
        params: List<String>,
        instrumentService: InstrumentService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        println("Name")
        val name = readerService.readCommand().joinToString("")
        println("Instrument type:" +
                "PH_METER = 1," +
                " BALANCE = 2, " +
                "SPECTROPHOTOMETER, = 3" +
                "CONDUCTIVITY_METER, = 4 " +
                "THERMOMETER= 5 ")
        val typeAsNum = readerService.readCommand().joinToString("").toInt()
        if(typeAsNum !in 1..5) {
            println("Wrong device type")
            return true

        }
        val type = InstrumentType.entries[typeAsNum - 1]
        println("Input Inventory № ")
        val invNumber = readerService.readCommand().joinToString("")
        println("Add location")
        val location = readerService.readCommand().joinToString("")
        if(location == "") {
            println("Wrong, enter location")
        }
        println(instrumentService.add(name, type, invNumber, location).toString())
        return true

    }

    override fun help(): String = "InstAdd - add instrument"
}