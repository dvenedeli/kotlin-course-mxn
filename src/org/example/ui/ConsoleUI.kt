package org.example.ui

import java.time.LocalDate
import java.time.format.DateTimeParseException

object ConsoleUI {

    fun readInt(prompt: String): Int? {
        print(prompt)
        return readlnOrNull()?.toIntOrNull()
    }

    fun readDouble(prompt: String): Double? {
        print(prompt)
        return readlnOrNull()?.toDoubleOrNull()
    }

    fun readString(prompt: String): String {
        print(prompt)
        return readlnOrNull() ?: ""
    }

    fun readLocalDate(prompt: String): LocalDate? {
        while (true) {
            print(prompt)
            val input = readlnOrNull()
            if (input.isNullOrBlank()) return null
            try {
                return LocalDate.parse(input)
            } catch (e: DateTimeParseException) {
                println("Неверный формат даты. Используйте ГГГГ-ММ-ДД.")
            }
        }
    }

    fun readNotEmptyString(prompt: String): String {
        while (true) {
            val s = readString(prompt)
            if (s.isNotBlank()) return s
            println("Поле не может быть пустым.")
        }
    }
}