package org.example.data

import org.example.utils.FileFormat

object RepositoryFactory {
    fun create(filePath: String, format: FileFormat): NotaryCaseRepository = when (format) {
        FileFormat.JSON -> JsonNotaryCaseRepository(filePath)
        FileFormat.CSV -> CsvNotaryCaseRepository(filePath)
    }
}
