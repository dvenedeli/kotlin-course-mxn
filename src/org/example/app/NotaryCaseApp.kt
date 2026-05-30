package org.example.app

import org.example.data.PersistenceError
import org.example.data.RepositoryFactory
import org.example.domain.FeeStats
import org.example.domain.NotaryCaseStore
import org.example.domain.SortField
import org.example.models.NotaryCase
import org.example.utils.FileFormat

class NotaryCaseApp(
    private val store: NotaryCaseStore = NotaryCaseStore()
) {
    fun getAllCases(): List<NotaryCase> = store.getAllCases()

    fun findById(id: Int): NotaryCase? = store.findById(id)

    fun addCase(c: NotaryCase): NotaryCase = store.addCase(c)

    fun updateCase(id: Int, newData: NotaryCase): Boolean = store.updateCase(id, newData)

    fun deleteCase(id: Int): Boolean = store.deleteCase(id)

    fun search(query: String): List<NotaryCase> = store.search(query)

    fun sortBy(field: SortField, ascending: Boolean = true): List<NotaryCase> =
        store.sortBy(field, ascending)

    fun filterByType(caseType: String): List<NotaryCase> = store.filterByType(caseType)

    fun getFeeStatistics(): FeeStats = store.getFeeStatistics()

    fun loadFromFile(filePath: String, format: FileFormat): Result<Unit> {
        val repository = RepositoryFactory.create(filePath, format)
        return repository.load().map { cases ->
            store.replaceAll(cases)
        }
    }

    fun saveToFile(filePath: String, format: FileFormat): Result<Unit> {
        val repository = RepositoryFactory.create(filePath, format)
        return repository.save(store.getAllCases())
    }

    fun persistenceMessage(error: Throwable): String = when (error) {
        is PersistenceError -> error.message ?: "Ошибка сохранения данных"
        else -> "Неизвестная ошибка: ${error.message}"
    }
}
