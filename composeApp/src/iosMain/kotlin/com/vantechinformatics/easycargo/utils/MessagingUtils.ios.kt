package com.vantechinformatics.easycargo.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun sendBulkSms(recipients: List<SmsRecipient>): SmsSendResult {
    if (recipients.isEmpty()) return SmsSendResult(sent = 0, failed = 0)
    val first = recipients.first()
    val smsUrl = NSURL(string = "sms:${first.phone}&body=${first.message}")
    if (UIApplication.sharedApplication.canOpenURL(smsUrl)) {
        UIApplication.sharedApplication.openURL(smsUrl)
    }
    return SmsSendResult(sent = recipients.size, failed = 0)
}

actual fun openWhatsApp(phone: String, message: String) {
    val cleanPhone = phone.replace(Regex("[^+\\d]"), "")
    val url = NSURL(string = "https://wa.me/$cleanPhone?text=$message")
    if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
        UIApplication.sharedApplication.openURL(url)
    }
}

actual fun isWhatsAppInstalled(): Boolean {
    val url = NSURL(string = "whatsapp://")
    return url != null && UIApplication.sharedApplication.canOpenURL(url)
}

actual fun hasSmsPermission(): Boolean = true
