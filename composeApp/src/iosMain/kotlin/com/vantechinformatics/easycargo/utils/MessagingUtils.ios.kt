package com.vantechinformatics.easycargo.utils

import platform.Foundation.NSCharacterSet
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.UIKit.UIApplication

actual fun openWhatsApp(phone: String, message: String) {
    val cleanPhone = phone.replace(Regex("[^+\\d]"), "")
    @Suppress("CAST_NEVER_SUCCEEDS")
    val encodedMessage = (message as NSString)
        .stringByAddingPercentEncodingWithAllowedCharacters(
            NSCharacterSet.URLQueryAllowedCharacterSet
        ) ?: message
    val url = NSURL(string = "https://wa.me/$cleanPhone?text=$encodedMessage")
    if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
        UIApplication.sharedApplication.openURL(url)
    }
}

actual fun openViber(phone: String, message: String) {
    val cleanPhone = phone.replace(Regex("[^+\\d]"), "")
    @Suppress("CAST_NEVER_SUCCEEDS")
    val encodedMessage = (message as NSString)
        .stringByAddingPercentEncodingWithAllowedCharacters(
            NSCharacterSet.URLQueryAllowedCharacterSet
        ) ?: message
    val url = NSURL(string = "viber://chat?number=$cleanPhone&draft=$encodedMessage")
    if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
        UIApplication.sharedApplication.openURL(url)
    }
}

actual fun isWhatsAppInstalled(): Boolean {
    val url = NSURL(string = "whatsapp://")
    return url != null && UIApplication.sharedApplication.canOpenURL(url)
}

actual fun isViberInstalled(): Boolean {
    val url = NSURL(string = "viber://")
    return url != null && UIApplication.sharedApplication.canOpenURL(url)
}
