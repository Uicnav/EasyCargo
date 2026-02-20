package com.vantechinformatics.easycargo.utils

import platform.Foundation.NSCharacterSet
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.UIKit.UIApplication

actual fun sendBulkSms(recipients: List<SmsRecipient>): SmsSendResult {
    if (recipients.isEmpty()) return SmsSendResult(sent = 0, failed = 0)
    val first = recipients.first()
    @Suppress("CAST_NEVER_SUCCEEDS")
    val encodedBody = (first.message as NSString)
        .stringByAddingPercentEncodingWithAllowedCharacters(
            NSCharacterSet.URLQueryAllowedCharacterSet
        ) ?: first.message
    val smsUrl = NSURL(string = "sms:${first.phone}?body=$encodedBody")
    if (UIApplication.sharedApplication.canOpenURL(smsUrl)) {
        UIApplication.sharedApplication.openURL(smsUrl)
    }
    // iOS only opens SMS for the first recipient - report honestly
    return SmsSendResult(sent = 1, failed = 0)
}

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

actual fun isWhatsAppInstalled(): Boolean {
    val url = NSURL(string = "whatsapp://")
    return url != null && UIApplication.sharedApplication.canOpenURL(url)
}

actual fun hasSmsPermission(): Boolean = true
