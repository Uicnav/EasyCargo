package com.vantechinformatics.easycargo.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

private val LAUNCH_COUNT_KEY = intPreferencesKey("launch_count")
private val REVIEW_REQUESTED_KEY = booleanPreferencesKey("review_requested")

private const val TRIGGER_AT_LAUNCH = 5
private const val UI_SETTLE_DELAY_MS = 1500L

suspend fun maybeRequestReview(dataStore: DataStore<Preferences>) {
    val prefs = dataStore.data.first()
    if (prefs[REVIEW_REQUESTED_KEY] == true) return

    val next = (prefs[LAUNCH_COUNT_KEY] ?: 0) + 1
    dataStore.edit { it[LAUNCH_COUNT_KEY] = next }

    if (next >= TRIGGER_AT_LAUNCH) {
        dataStore.edit { it[REVIEW_REQUESTED_KEY] = true }
        delay(UI_SETTLE_DELAY_MS)
        requestAppReview()
    }
}
