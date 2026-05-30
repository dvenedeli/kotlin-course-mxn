package org.example.domain

import org.example.models.NotaryCase

class NotaryCaseStore(initialCases: List<NotaryCase> = emptyList()) {
    private val cases = initialCases.toMutableList()
    private var nextId = (initialCases.maxOfOrNull { it.id } ?: 0) + 1

    fun getAllCases(): List<NotaryCase> = cases.toList()

    fun findById(id: Int): NotaryCase? = cases.find { it.id == id }

    fun addCase(c: NotaryCase): NotaryCase {
        val newCase = c.copy(id = nextId++)
        cases.add(newCase)
        return newCase
    }

    fun updateCase(id: Int, newData: NotaryCase): Boolean {
        val index = cases.indexOfFirst { it.id == id }
        if (index == -1) return false
        cases[index] = newData.copy(id = id)
        return true
    }

    fun deleteCase(id: Int): Boolean = cases.removeIf { it.id == id }

    fun replaceAll(loadedCases: List<NotaryCase>) {
        cases.clear()
        cases.addAll(loadedCases)
        nextId = (cases.maxOfOrNull { it.id } ?: 0) + 1
    }

    fun search(query: String): List<NotaryCase> {
        val lowerQuery = query.lowercase()
        return cases.filter {
            it.clientName.lowercase().contains(lowerQuery) ||
                it.caseNumber.lowercase().contains(lowerQuery) ||
                it.caseType.lowercase().contains(lowerQuery)
        }
    }

    fun sortBy(field: SortField, ascending: Boolean = true): List<NotaryCase> {
        val comparator: Comparator<NotaryCase> = when (field) {
            SortField.ID -> compareBy { it.id }
            SortField.CASE_NUMBER -> compareBy { it.caseNumber }
            SortField.CLIENT_NAME -> compareBy { it.clientName }
            SortField.OPEN_DATE -> compareBy { it.openDate }
            SortField.FEE -> compareBy { it.fee }
        }
        val finalComparator = if (ascending) comparator else comparator.reversed()
        return cases.sortedWith(finalComparator)
    }

    fun filterByType(caseType: String): List<NotaryCase> {
        val lowerType = caseType.lowercase()
        return cases.filter { it.caseType.lowercase().contains(lowerType) }
    }

    fun getFeeStatistics(): FeeStats {
        if (cases.isEmpty()) return FeeStats(0.0, 0.0, 0.0, 0.0)
        val fees = cases.map { it.fee }
        return FeeStats(
            average = fees.average(),
            sum = fees.sum(),
            min = fees.minOrNull()!!,
            max = fees.maxOrNull()!!
        )
    }
}
