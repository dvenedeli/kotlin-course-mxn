package org.example.data

import org.example.models.NotaryCase
import java.time.LocalDate
import java.time.format.DateTimeParseException

object CsvNotaryCaseCodec {
    private const val HEADER = "id,caseNumber,clientName,caseType,openDate,closeDate,fee"

    fun encode(cases: List<NotaryCase>): String = buildString {
        appendLine(HEADER)
        cases.forEach { appendLine(encodeLine(it)) }
    }

    fun encodeLine(case: NotaryCase): String =
        "${case.id},${case.caseNumber},${case.clientName},${case.caseType},${case.openDate},${case.closeDate ?: ""},${case.fee}"

    fun decode(content: String): Result<List<NotaryCase>> {
        val lines = content.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return Result.success(emptyList())

        val dataLines = if (lines.first().startsWith("id,")) lines.drop(1) else lines
        val cases = mutableListOf<NotaryCase>()
        val errors = mutableListOf<String>()

        dataLines.forEach { line ->
            decodeLine(line)
                .onSuccess { cases.add(it) }
                .onFailure { errors.add(it.message ?: "Ошибка парсинга строки CSV: $line") }
        }

        return if (errors.isNotEmpty()) {
            Result.failure(PersistenceError.ParseError(errors.joinToString("; ")))
        } else {
            Result.success(cases)
        }
    }

    fun decodeLine(line: String): Result<NotaryCase> {
        val parts = line.split(',')
        if (parts.size != 7) {
            return Result.failure(PersistenceError.ParseError("Неверное количество полей в строке CSV: $line"))
        }

        return try {
            val id = parts[0].toInt()
            val caseNumber = parts[1]
            val clientName = parts[2]
            val caseType = parts[3]
            val openDate = LocalDate.parse(parts[4])
            val closeDate = if (parts[5].isNotBlank()) LocalDate.parse(parts[5]) else null
            val fee = parts[6].toDouble()
            Result.success(
                NotaryCase(id, caseNumber, clientName, caseType, openDate, closeDate, fee)
            )
        } catch (e: NumberFormatException) {
            Result.failure(PersistenceError.ParseError("Неверный числовой формат в строке CSV: $line"))
        } catch (e: DateTimeParseException) {
            Result.failure(PersistenceError.ParseError("Неверный формат даты в строке CSV: $line"))
        }
    }
}
