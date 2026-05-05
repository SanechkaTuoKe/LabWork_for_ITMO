package org.example.cli.service

import org.example.cli.services.CommandService
import org.junit.jupiter.api.Test

class CommandServiceSimpleTest {

    @Test
    fun `debug - test commands`() {
        val service = CommandService()

        println("=== Testing exit ===")
        val exitResult = service.execute(listOf("exit"))
        println("Exit result: $exitResult")

        println("\n=== Testing inst_list ===")
        val listResult = service.execute(listOf("inst_list"))
        println("List result: $listResult")
    }
}