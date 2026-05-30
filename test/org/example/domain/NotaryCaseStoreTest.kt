package org.example.domain

import org.example.models.NotaryCase
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NotaryCaseStoreTest {

    private fun sampleCase(
        id: Int = 0,
        caseNumber: String = "001",
        clientName: String = "Иванов И.И.",
        caseType: String = "Доверенность",
        fee: Double = 1000.0
    ) = NotaryCase(
        id = id,
        caseNumber = caseNumber,
        clientName = clientName,
        caseType = caseType,
        openDate = LocalDate.of(2024, 1, 10),
        closeDate = null,
        fee = fee
    )

    @Test
    fun addCaseAssignsIncrementalIds() {
        val store = NotaryCaseStore()
        val first = store.addCase(sampleCase(caseNumber = "1"))
        val second = store.addCase(sampleCase(caseNumber = "2"))

        assertEquals(1, first.id)
        assertEquals(2, second.id)
        assertEquals(2, store.getAllCases().size)
    }

    @Test
    fun replaceAllResetsNextId() {
        val store = NotaryCaseStore()
        store.addCase(sampleCase())
        store.replaceAll(
            listOf(
                sampleCase(id = 5, caseNumber = "A"),
                sampleCase(id = 10, caseNumber = "B")
            )
        )

        val added = store.addCase(sampleCase(caseNumber = "C"))
        assertEquals(11, added.id)
        assertEquals(3, store.getAllCases().size)
    }

    @Test
    fun updateAndDeleteCase() {
        val store = NotaryCaseStore()
        val added = store.addCase(sampleCase(clientName = "Петров"))

        assertTrue(store.updateCase(added.id, added.copy(clientName = "Сидоров")))
        assertEquals("Сидоров", store.findById(added.id)?.clientName)

        assertTrue(store.deleteCase(added.id))
        assertNull(store.findById(added.id))
        assertFalse(store.updateCase(added.id, added))
    }

    @Test
    fun searchIsCaseInsensitive() {
        val store = NotaryCaseStore(
            listOf(
                sampleCase(id = 1, clientName = "Иванов", caseNumber = "001", caseType = "Доверенность"),
                sampleCase(id = 2, clientName = "Петров", caseNumber = "002", caseType = "Завещание")
            )
        )

        assertEquals(1, store.search("иван").size)
        assertEquals(1, store.search("002").size)
        assertEquals(1, store.search("ЗАВЕ").size)
    }

    @Test
    fun sortByFeeAscendingAndDescending() {
        val store = NotaryCaseStore(
            listOf(
                sampleCase(id = 1, fee = 300.0),
                sampleCase(id = 2, fee = 100.0),
                sampleCase(id = 3, fee = 200.0)
            )
        )

        assertEquals(listOf(100.0, 200.0, 300.0), store.sortBy(SortField.FEE).map { it.fee })
        assertEquals(listOf(300.0, 200.0, 100.0), store.sortBy(SortField.FEE, ascending = false).map { it.fee })
    }

    @Test
    fun filterByTypeMatchesPartialCaseInsensitive() {
        val store = NotaryCaseStore(
            listOf(
                sampleCase(id = 1, caseType = "Доверенность"),
                sampleCase(id = 2, caseType = "Завещание")
            )
        )

        assertEquals(1, store.filterByType("довер").size)
        assertEquals(0, store.filterByType("продажа").size)
    }

    @Test
    fun feeStatisticsForEmptyAndNonEmptyLists() {
        val emptyStore = NotaryCaseStore()
        assertEquals(FeeStats(0.0, 0.0, 0.0, 0.0), emptyStore.getFeeStatistics())

        val store = NotaryCaseStore(
            listOf(
                sampleCase(id = 1, fee = 100.0),
                sampleCase(id = 2, fee = 300.0)
            )
        )
        val stats = store.getFeeStatistics()
        assertEquals(200.0, stats.average)
        assertEquals(400.0, stats.sum)
        assertEquals(100.0, stats.min)
        assertEquals(300.0, stats.max)
    }
}
