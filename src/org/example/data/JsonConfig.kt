package org.example.data

import kotlinx.serialization.json.Json

object JsonConfig {
    val json: Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
}
