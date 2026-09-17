package com.kraft.calculator.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "kalc_settings")

enum class AppTheme { SYSTEM, LIGHT, DARK, AMOLED }

data class AppSettings(
    val vibrationEnabled: Boolean = true,
    val theme: AppTheme = AppTheme.SYSTEM,
    val decimalPrecision: Int = 10,
    val historySize: Int = 50,
)

class SettingsRepository(private val context: Context) {

    private object Keys {
        val VIBRATION = booleanPreferencesKey("vibration_enabled")
        val THEME = stringPreferencesKey("app_theme")
        val PRECISION = intPreferencesKey("decimal_precision")
        val HISTORY_SIZE = intPreferencesKey("history_size")
    }

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            vibrationEnabled = prefs[Keys.VIBRATION] ?: true,
            theme = try {
                AppTheme.valueOf(prefs[Keys.THEME] ?: AppTheme.SYSTEM.name)
            } catch (_: Exception) {
                AppTheme.SYSTEM
            },
            decimalPrecision = (prefs[Keys.PRECISION] ?: 10).coerceIn(2, 15),
            historySize = (prefs[Keys.HISTORY_SIZE] ?: 50).coerceIn(0, 500),
        )
    }

    suspend fun setVibration(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.VIBRATION] = enabled }
    }

    suspend fun setTheme(theme: AppTheme) {
        context.settingsDataStore.edit { it[Keys.THEME] = theme.name }
    }

    suspend fun setPrecision(precision: Int) {
        context.settingsDataStore.edit { it[Keys.PRECISION] = precision.coerceIn(2, 15) }
    }

    suspend fun setHistorySize(size: Int) {
        context.settingsDataStore.edit { it[Keys.HISTORY_SIZE] = size.coerceIn(0, 500) }
    }
}
