package com.vantechinformatics.easycargo.utils

import platform.Foundation.NSUserDefaults

actual fun setAppLocale(languageCode: String) {
    val defaults = NSUserDefaults.standardUserDefaults
    if (languageCode.isEmpty()) {
        defaults.removeObjectForKey("AppleLanguages")
    } else {
        defaults.setObject(listOf(languageCode), forKey = "AppleLanguages")
    }
}

actual fun getAppLocale(): String? {
    val defaults = NSUserDefaults.standardUserDefaults
    val languages = defaults.objectForKey("AppleLanguages") as? List<*>
    return languages?.firstOrNull() as? String
}
