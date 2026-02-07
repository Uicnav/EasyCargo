package com.vantechinformatics.easycargo.utils

import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import java.util.Locale

actual fun setAppLocale(languageCode: String) {
    if (languageCode.isNotEmpty()) {
        Locale.setDefault(Locale(languageCode))
    } else {
        val systemLocale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
        if (systemLocale != null) {
            Locale.setDefault(systemLocale)
        }
    }
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
