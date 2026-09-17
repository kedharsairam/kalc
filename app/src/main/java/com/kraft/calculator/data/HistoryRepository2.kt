package com.kraft.calculator.data

import android.content.Context
import com.kraft.calculator.domain.CalculationEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-backed history repository with stable UUIDs, size cap, and
 * one-time migration from legacy SharedPreferences storage.
 */
class RoomHistoryRepository(context: Context) {

    private val appContext = context.applicationContext
    private val db = HistoryDatabase.get(appContext)
    private val dao = db.historyDao()
    private val legacy = HistoryRepository(appContext)

    @Volatile
    private var migrated = false

    fun observe(limit: Int): Flow<List<CalculationEntry>> {
        return dao.observe(limit).map { entities ->
            entities.map { CalculationEntry(it.expression, it.result, it.timestamp) }
        }
    }

    suspend fun loadHistory(limit: Int = 100): List<CalculationEntry> {
        migrateIfNeeded(limit)
        return dao.getRecent(limit).map {
            CalculationEntry(it.expression, it.result, it.timestamp)
        }
    }

    suspend fun saveEntry(expression: String, result: String, maxSize: Int = 100) {
        migrateIfNeeded(maxSize)
        dao.insert(HistoryEntity(expression = expression, result = result))
        if (maxSize > 0) {
            dao.trimTo(maxSize)
        }
    }

    suspend fun deleteByExpression(expression: String, timestamp: Long) {
        // Find by expression+timestamp since legacy entries lack stable IDs
        val all = dao.getRecent(500)
        val match = all.firstOrNull { it.expression == expression && it.timestamp == timestamp }
        match?.let { dao.deleteById(it.id) }
    }

    suspend fun clearAll() {
        dao.clearAll()
    }

    private suspend fun migrateIfNeeded(limit: Int) {
        if (migrated) return
        synchronized(this) {
            if (migrated) return
        }
        try {
            if (dao.count() == 0) {
                val legacyEntries = legacy.loadHistory()
                val toKeep = if (limit > 0) legacyEntries.take(limit) else legacyEntries
                toKeep.forEach { entry ->
                    dao.insert(
                        HistoryEntity(
                            expression = entry.expression,
                            result = entry.result,
                            timestamp = entry.timestamp,
                        )
                    )
                }
                if (toKeep.isNotEmpty()) {
                    legacy.clearHistory()
                }
            }
        } catch (_: Exception) {
            // Migration best-effort; Room starts fresh on failure
        } finally {
            migrated = true
        }
    }
}
