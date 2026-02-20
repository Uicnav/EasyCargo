package com.vantechinformatics.easycargo.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.SmsManager
import androidx.core.content.ContextCompat

private var messagingContext: Context? = null

fun initMessagingContext(context: Context) {
    messagingContext = context.applicationContext
}

actual fun sendBulkSms(recipients: List<SmsRecipient>): SmsSendResult {
    val ctx = messagingContext ?: return SmsSendResult(sent = 0, failed = recipients.size)
    var sent = 0
    var failed = 0

    @Suppress("DEPRECATION")
    val smsManager = SmsManager.getDefault()

    for (recipient in recipients) {
        try {
            smsManager.sendTextMessage(
                recipient.phone,
                null,
                recipient.message,
                null,
                null
            )
            sent++
        } catch (e: Exception) {
            failed++
        }
    }
    return SmsSendResult(sent = sent, failed = failed)
}

actual fun openWhatsApp(phone: String, message: String) {
    val ctx = messagingContext ?: return
    val cleanPhone = phone.replace(Regex("[^+\\d]"), "")
    val encodedMessage = Uri.encode(message)
    val uri = Uri.parse("https://wa.me/$cleanPhone?text=$encodedMessage")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    ctx.startActivity(intent)
}

actual fun isWhatsAppInstalled(): Boolean {
    val ctx = messagingContext ?: return false
    return try {
        ctx.packageManager.getPackageInfo("com.whatsapp", 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

actual fun hasSmsPermission(): Boolean {
    val ctx = messagingContext ?: return false
    return ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS) ==
        PackageManager.PERMISSION_GRANTED
}
