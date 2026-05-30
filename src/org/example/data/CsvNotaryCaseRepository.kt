package org.example.data

import org.example.models.NotaryCase
import java.io.File

class CsvNotaryCaseRepository(
    private val filePath: String
) : NotaryCaseRepository {

    override fun load(): Result<List<NotaryCase>> {
        val file = File(filePath)
        if (!file.exists()) {
            return Result.failure(PersistenceError.FileNotFound(filePath))
        }

        return try {
            CsvNotaryCaseCodec.decode(file.readText())
        } catch (e: java.io.IOException) {
            Result.failure(PersistenceError.IoError("Ошибка чтения файла: ${e.message}"))
        }
    }

    override fun save(cases: List<NotaryCase>): Result<Unit> {
        return try {
            File(filePath).writeText(CsvNotaryCaseCodec.encode(cases))
            Result.success(Unit)
        } catch (e: java.io.IOException) {
            Result.failure(PersistenceError.IoError("Ошибка записи файла: ${e.message}"))
        }
    }
}
