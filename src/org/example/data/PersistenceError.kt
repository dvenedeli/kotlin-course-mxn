package org.example.data

sealed class PersistenceError(message: String) : Exception(message) {
    class FileNotFound(path: String) : PersistenceError("Файл не найден: $path")
    class ParseError(message: String) : PersistenceError(message)
    class IoError(message: String) : PersistenceError(message)
}
