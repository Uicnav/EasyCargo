package com.vantechinformatics.easycargo.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

private var messagingContext: Context? = null

fun initMessagingContext(context: Context) {
    messagingContext = context.applicationContext
}

actual fun openWhatsApp(phone: String, message: String) {
    val ctx = messagingContext ?: return
    val cleanPhone = phone.replace(Regex("[^\\d]"), "")
    val encodedMessage = Uri.encode(message)
    val uri = Uri.parse("https://wa.me/$cleanPhone?text=$encodedMessage")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    ctx.startActivity(intent)
}

actual fun openViber(phone: String, message: String) {
    val ctx = messagingContext ?: return
    val cleanPhone = phone.replace(Regex("[^+\\d]"), "")
    val encodedMessage = Uri.encode(message)
    val uri = Uri.parse("viber://chat?number=$cleanPhone&draft=$encodedMessage")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        ctx.startActivity(intent)
    } catch (e: Exception) {
        // Fallback: open Viber without draft
        val fallback = Intent(Intent.ACTION_VIEW, Uri.parse("viber://chat?number=$cleanPhone"))
        fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(fallback)
    }
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

actual fun isViberInstalled(): Boolean {
    val ctx = messagingContext ?: return false
    return try {
        ctx.packageManager.getPackageInfo("com.viber.voip", 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}
