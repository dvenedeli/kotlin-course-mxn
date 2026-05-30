package org.example.app

import org.example.models.NotaryCase
import org.example.utils.FileFormat
import java.nio.file.Files
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotaryCaseAppTest {

    @Test
    fun loadAndSaveThroughAppUpdatesStore() {
        val tempFile = Files.createTempFile("notary-app", ".json")
        tempFile.toFile().deleteOnExit()
        val app = NotaryCaseApp()

        app.addCase(
            NotaryCase(
                id = 0,
                caseNumber = "001",
                clientName = "Иванов",
                caseType = "Доверенность",
                openDate = LocalDate.of(2024, 1, 1),
                closeDate = null,
                fee = 100.0
            )
        )

        app.saveToFile(tempFile.toString(), FileFormat.JSON).getOrThrow()

        val reloadedApp = NotaryCaseApp()
        reloadedApp.loadFromFile(tempFile.toString(), FileFormat.JSON).getOrThrow()

        assertEquals(1, reloadedApp.getAllCases().size)
        assertEquals("Иванов", reloadedApp.getAllCases().first().clientName)
    }

    @Test
    fun loadMissingFileReturnsFailure() {
        val app = NotaryCaseApp()
        val result = app.loadFromFile("missing-${System.nanoTime()}.json", FileFormat.JSON)

        assertTrue(result.isFailure)
    }
}
