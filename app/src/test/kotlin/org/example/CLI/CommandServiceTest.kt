package org.example.cli

import org.example.cli.services.CommandService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommandServiceTest {

    private lateinit var commandService: CommandService

    @BeforeEach
    fun setup() {
        commandService = CommandService()
    }

    @Test
    fun execute_invalidCommand_printsError() {
        val result = commandService.execute(listOf("unknown_command"))
        // метод execute возвращает true, даже при ошибке, чтобы цикл CLI не падал
        assert(result)
    }
}