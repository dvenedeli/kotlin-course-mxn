package org.example.data

import org.example.models.NotaryCase
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CsvNotaryCaseCodecTest {

    private fun sampleCase(id: Int = 1) = NotaryCase(
        id = id,
        caseNumber = "001",
        clientName = "Иванов И.И.",
        caseType = "Доверенность",
        openDate = LocalDate.of(2024, 5, 1),
        closeDate = LocalDate.of(2024, 6, 1),
        fee = 1500.5
    )

    @Test
    fun encodeAndDecodeRoundTrip() {
        val cases = listOf(sampleCase(1), sampleCase(2).copy(caseNumber = "002", closeDate = null))
        val encoded = CsvNotaryCaseCodec.encode(cases)
        val decoded = CsvNotaryCaseCodec.decode(encoded).getOrThrow()

        assertEquals(cases, decoded)
    }

    @Test
    fun decodeSkipsHeaderWhenPresent() {
        val line = CsvNotaryCaseCodec.encodeLine(sampleCase())
        val content = "id,caseNumber,clientName,caseType,openDate,closeDate,fee\n$line"
        val decoded = CsvNotaryCaseCodec.decode(content).getOrThrow()

        assertEquals(1, decoded.size)
        assertEquals(sampleCase(), decoded.first())
    }

    @Test
    fun decodeLineFailsForInvalidFieldCount() {
        val result = CsvNotaryCaseCodec.decodeLine("1,2,3")
        assertTrue(result.isFailure)
    }

    @Test
    fun decodeEmptyContentReturnsEmptyList() {
        val decoded = CsvNotaryCaseCodec.decode("").getOrThrow()
        assertTrue(decoded.isEmpty())
    }
}
