package org.example.cli.services

class ReaderService {

    fun readCommand(): List<String> {
        val input = readln()
        return input.trim().split(" ")
    }
}