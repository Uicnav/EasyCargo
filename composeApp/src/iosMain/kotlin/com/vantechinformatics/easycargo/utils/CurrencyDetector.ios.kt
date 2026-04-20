package com.vantechinformatics.easycargo.utils

import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.NSLocaleCurrencySymbol
import platform.Foundation.currentLocale

actual fun getCurrencySymbol(): String {
    val locale = NSLocale.currentLocale
    val region = locale.objectForKey(NSLocaleCountryCode) as? String
    if (isInEurope(region)) return "€"
    val symbol = locale.objectForKey(NSLocaleCurrencySymbol) as? String
    return symbol ?: "€"
}
