package com.vantechinformatics.easycargo.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

actual fun setAppLocale(languageCode: String) {
    val localeList = if (languageCode.isEmpty()) {
        LocaleListCompat.getEmptyLocaleList()
    } else {
        LocaleListCompat.forLanguageTags(languageCode)
    }
    AppCompatDelegate.setApplicationLocales(localeList)
}

actual fun getAppLocale(): String? {
    val locales = AppCompatDelegate.getApplicationLocales()
    if (locales.isEmpty) return null
    return locales[0]?.language
}
