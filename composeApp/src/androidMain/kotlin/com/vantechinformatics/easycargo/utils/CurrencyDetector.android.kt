package com.vantechinformatics.easycargo.utils

import java.util.Currency
import java.util.Locale

actual fun getCurrencySymbol(): String {
    val locale = Locale.getDefault()
    if (isInEurope(locale.country)) return "€"
    return runCatching { Currency.getInstance(locale).symbol }.getOrNull() ?: "€"
}
