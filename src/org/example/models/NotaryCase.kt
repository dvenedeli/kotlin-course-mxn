package org.example.models

import kotlinx.serialization.Serializable
import org.example.serialization.LocalDateSerializer
import org.example.serialization.NullableLocalDateSerializer
import java.time.LocalDate

@Serializable
data class NotaryCase(
    val id: Int,
    val caseNumber: String,
    val clientName: String,
    val caseType: String,
    @Serializable(with = LocalDateSerializer::class)
    val openDate: LocalDate,
    @Serializable(with = NullableLocalDateSerializer::class)
    val closeDate: LocalDate?,
    val fee: Double
)