package com.vantechinformatics.easycargo.utils

// Country codes treated as "Europe" → always show EUR, regardless of device
// locale currency (covers EU, Schengen, EEA, micro-states, candidates).
private val EUROPEAN_COUNTRIES = setOf(
    "AD", "AL", "AT", "BA", "BE", "BG", "BY", "CH", "CY", "CZ",
    "DE", "DK", "EE", "ES", "FI", "FO", "FR", "GB", "GE", "GI",
    "GR", "HR", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV",
    "MC", "MD", "ME", "MK", "MT", "NL", "NO", "PL", "PT", "RO",
    "RS", "RU", "SE", "SI", "SK", "SM", "TR", "UA", "VA", "XK"
)

fun isInEurope(regionCode: String?): Boolean =
    regionCode != null && regionCode.uppercase() in EUROPEAN_COUNTRIES

// Returns the currency symbol to display next to amounts.
// Europe → "€", otherwise the symbol for the device's region (e.g. "$", "£", "₽").
expect fun getCurrencySymbol(): String
