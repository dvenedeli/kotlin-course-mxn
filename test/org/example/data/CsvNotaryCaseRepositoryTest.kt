package org.example.data

import org.example.models.NotaryCase
import java.nio.file.Files
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CsvNotaryCaseRepositoryTest {

    private fun sampleCases() = listOf(
        NotaryCase(
            id = 1,
            caseNumber = "001",
            clientName = "Иванов",
            caseType = "Доверенность",
            openDate = LocalDate.of(2024, 1, 1),
            closeDate = null,
            fee = 500.0
        )
    )

    @Test
    fun saveAndLoadRoundTrip() {
        val tempFile = Files.createTempFile("notary-cases", ".csv")
        tempFile.toFile().deleteOnExit()
        val repository = CsvNotaryCaseRepository(tempFile.toString())
        val cases = sampleCases()

        repository.save(cases).getOrThrow()
        val loaded = repository.load().getOrThrow()

        assertEquals(cases, loaded)
    }

    @Test
    fun loadFailsWhenFileMissing() {
        val repository = CsvNotaryCaseRepository("missing-file-${System.nanoTime()}.csv")
        val result = repository.load()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is PersistenceError.FileNotFound)
    }
}
