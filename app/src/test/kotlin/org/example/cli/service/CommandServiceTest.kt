package org.example.cli.service

import org.example.cli.services.CommandService
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class CommandServiceTest {

    private val commandService = CommandService()

    @Test
    fun `execute - should handle invalid command`() {
        val result = commandService.execute(listOf("unknown_command"))
        assertTrue(result)
    }

    @Test
    fun `execute - should handle inst_list`() {
        val result = commandService.execute(listOf("inst_list"))
        assertTrue(result)
    }

    @Test
    fun `execute - should handle help`() {
        val result = commandService.execute(listOf("help"))
        assertTrue(result)
    }

    @Test
    fun `execute - should handle empty input`() {
        val result = commandService.execute(emptyList())
        assertTrue(result)
    }

    @Test
    fun `execute - should handle exit`() {
        commandService.execute(listOf("exit"))
        // Просто проверяем, что нет исключений
        assertTrue(true)
    }
}