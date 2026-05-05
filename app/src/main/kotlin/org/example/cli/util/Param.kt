package org.example.cli.util

object Param {
    fun paramValue(commandLine : List<String>, subCommand : String) : String? {
        val paramIndex = commandLine.indexOf("--$subCommand")
        if(paramIndex == -1 || paramIndex == commandLine.size - 1) {
            return null
        }
        return commandLine[paramIndex + 1]
    }
}