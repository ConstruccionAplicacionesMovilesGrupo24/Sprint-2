package com.campusmeal.android.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val PREFERENCES_NAME = "campusmeal_preferences"

private val Context.campusMealPreferences: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

/**
 * Single DataStore instance for non-sensitive user preferences.
 * DataStore files are plaintext: never store tokens or credentials here; use SessionStorage.
 */
object CampusMealPreferences {
    fun dataStore(context: Context): DataStore<Preferences> = context.applicationContext.campusMealPreferences
}
