package org.example.ui

import org.example.models.NotaryCase

object NotaryCaseFormatter {
    fun toShortString(case: NotaryCase): String =
        "ID: ${case.id} | № ${case.caseNumber} | ${case.clientName} | ${case.caseType} | " +
            "${case.openDate} | ${case.closeDate ?: "в работе"} | ${case.fee} руб."
}
