package com.kraft.calculator.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class CalculationEntry(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis(),
) {
    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromJson(jsonString: String): CalculationEntry {
            return json.decodeFromString(jsonString)
        }
    }

    fun toJson(): String = json.encodeToString(this)
}
