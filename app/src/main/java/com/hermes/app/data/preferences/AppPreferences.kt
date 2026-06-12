package com.hermes.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hermes_settings")

class AppPreferences(private val context: Context) {

    companion object {
        private val KEY_HOST = stringPreferencesKey("host")
        private val KEY_PORT = intPreferencesKey("port")
        private val KEY_API_KEY = stringPreferencesKey("api_key")
        private val KEY_THEME = stringPreferencesKey("theme")

        const val DEFAULT_HOST = "127.0.0.1"
        const val DEFAULT_PORT = 8642
    }

    val host: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_HOST] ?: DEFAULT_HOST
    }

    val port: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_PORT] ?: DEFAULT_PORT
    }

    val apiKey: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_API_KEY] ?: ""
    }

    val baseUrl: Flow<String> = context.dataStore.data.map { prefs ->
        val h = prefs[KEY_HOST] ?: DEFAULT_HOST
        val p = prefs[KEY_PORT] ?: DEFAULT_PORT
        "http://$h:$p"
    }

    suspend fun saveHost(host: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_HOST] = host
        }
    }

    suspend fun savePort(port: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PORT] = port
        }
    }

    suspend fun saveApiKey(key: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_API_KEY] = key
        }
    }
}
