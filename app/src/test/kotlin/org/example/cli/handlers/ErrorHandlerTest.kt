package org.example.cli.handlers

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertTrue

class ErrorHandlerTest {

    private lateinit var outContent: ByteArrayOutputStream
    private val originalOut = System.out

    @BeforeEach
    fun setup() {
        outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))
    }

    @AfterEach
    fun tearDown() {
        System.setOut(originalOut)
    }

    @Test
    fun `handle - should print error message`() {
        val exception = IllegalArgumentException("Test error")

        ErrorHandler.handle(exception)

        val output = outContent.toString()
        assertTrue(output.contains("Error: Test error"))
    }

    @Test
    fun `handle - should handle null message`() {
        ErrorHandler.handle(Exception())

        val output = outContent.toString()
        assertTrue(output.contains("Error:"))
    }
}