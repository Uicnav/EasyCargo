package com.vantechinformatics.easycargo.utils

import androidx.datastore.preferences.core.stringPreferencesKey

val LANGUAGE_KEY = stringPreferencesKey("app_language")

data class AppLanguage(val code: String, val displayName: String)

val SUPPORTED_LANGUAGES = listOf(
    AppLanguage("ro", "Romana"),
    AppLanguage("en", "English"),
    AppLanguage("it", "Italiano"),
    AppLanguage("de", "Deutsch"),
    AppLanguage("fr", "Francais"),
    AppLanguage("es", "Espanol"),
)

expect fun setAppLocale(languageCode: String)

expect fun getAppLocale(): String?
