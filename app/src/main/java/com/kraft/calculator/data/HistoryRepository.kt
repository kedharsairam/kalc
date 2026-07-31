package com.kraft.calculator.data

import android.content.Context
import android.content.SharedPreferences
import com.kraft.calculator.domain.CalculationEntry

class HistoryRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadHistory(): List<CalculationEntry> {
        val allKeys = prefs.getString(KEYS_KEY, null) ?: return emptyList()
        val keys = allKeys.split(SEPARATOR)
        val entries = mutableListOf<CalculationEntry>()
        for (key in keys) {
            val json = prefs.getString(key, null) ?: continue
            try {
                entries.add(CalculationEntry.fromJson(json))
            } catch (_: Exception) { /* skip corrupt entries */ }
        }
        return entries
    }

    fun saveHistory(entries: List<CalculationEntry>) {
        val editor = prefs.edit()
        // Clear old entries
        val oldKeys = prefs.getString(KEYS_KEY, null)
        if (oldKeys != null) {
            for (key in oldKeys.split(SEPARATOR)) {
                editor.remove(key)
            }
        }
        // Write new entries
        val keys = entries.map { entry ->
            val key = "entry_${entry.timestamp}"
            editor.putString(key, entry.toJson())
            key
        }
        editor.putString(KEYS_KEY, keys.joinToString(SEPARATOR))
        editor.apply()
    }

    fun clearHistory() {
        val keys = prefs.getString(KEYS_KEY, null)
        if (keys != null) {
            prefs.edit().apply {
                for (key in keys.split(SEPARATOR)) {
                    remove(key)
                }
                remove(KEYS_KEY)
                apply()
            }
        }
    }

    companion object {
        private const val PREFS_NAME = "calculator_history"
        private const val KEYS_KEY = "entry_keys"
        private const val SEPARATOR = ","
    }
}
