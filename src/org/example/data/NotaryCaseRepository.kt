package org.example.data

import org.example.models.NotaryCase

interface NotaryCaseRepository {
    fun load(): Result<List<NotaryCase>>
    fun save(cases: List<NotaryCase>): Result<Unit>
}
