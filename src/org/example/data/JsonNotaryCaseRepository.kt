package org.example.data

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.models.NotaryCase
import java.io.File

class JsonNotaryCaseRepository(
    private val filePath: String,
    private val json: Json = JsonConfig.json
) : NotaryCaseRepository {

    override fun load(): Result<List<NotaryCase>> {
        val file = File(filePath)
        if (!file.exists()) {
            return Result.failure(PersistenceError.FileNotFound(filePath))
        }

        return try {
            val loadedCases: List<NotaryCase> = json.decodeFromString(file.readText())
            Result.success(loadedCases)
        } catch (e: kotlinx.serialization.SerializationException) {
            Result.failure(PersistenceError.ParseError("Ошибка парсинга JSON: ${e.message}"))
        } catch (e: java.io.IOException) {
            Result.failure(PersistenceError.IoError("Ошибка чтения файла: ${e.message}"))
        }
    }

    override fun save(cases: List<NotaryCase>): Result<Unit> {
        return try {
            File(filePath).writeText(json.encodeToString(cases))
            Result.success(Unit)
        } catch (e: java.io.IOException) {
            Result.failure(PersistenceError.IoError("Ошибка записи файла: ${e.message}"))
        }
    }
}
