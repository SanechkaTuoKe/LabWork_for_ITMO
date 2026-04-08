package org.example

import LoopService


fun main(args: Array<String>) {
    println("type help for help")

    val loopService = LoopService()

    //возможность указать путь при запуске
    if (args.isNotEmpty()) {
        val path = args[0]
        println("Your path?: $path")
        loopService.loadInitialData(path)
    }

    loopService.loopOfCommands()
}

