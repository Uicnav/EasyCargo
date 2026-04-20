package com.vantechinformatics.easycargo.utils

import androidx.datastore.preferences.core.stringPreferencesKey

val LANGUAGE_KEY = stringPreferencesKey("app_language")

data class AppLanguage(val code: String, val displayName: String)

val SUPPORTED_LANGUAGES = listOf(
    AppLanguage("ro", "Română"),
    AppLanguage("en", "English"),
    AppLanguage("bg", "Български"),
    AppLanguage("cs", "Čeština"),
    AppLanguage("da", "Dansk"),
    AppLanguage("de", "Deutsch"),
    AppLanguage("el", "Ελληνικά"),
    AppLanguage("es", "Español"),
    AppLanguage("fi", "Suomi"),
    AppLanguage("fr", "Français"),
    AppLanguage("hr", "Hrvatski"),
    AppLanguage("hu", "Magyar"),
    AppLanguage("it", "Italiano"),
    AppLanguage("nl", "Nederlands"),
    AppLanguage("no", "Norsk"),
    AppLanguage("pl", "Polski"),
    AppLanguage("pt", "Português"),
    AppLanguage("ru", "Русский"),
    AppLanguage("sk", "Slovenčina"),
    AppLanguage("sq", "Shqip"),
    AppLanguage("sr", "Српски"),
    AppLanguage("sv", "Svenska"),
    AppLanguage("tr", "Türkçe"),
    AppLanguage("uk", "Українська"),
)

expect fun setAppLocale(languageCode: String)

expect fun getAppLocale(): String?
