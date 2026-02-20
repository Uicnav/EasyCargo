package com.vantechinformatics.easycargo.utils

data class SmsRecipient(val phone: String, val message: String)

data class SmsSendResult(val sent: Int, val failed: Int)

expect fun sendBulkSms(recipients: List<SmsRecipient>): SmsSendResult

expect fun openWhatsApp(phone: String, message: String)

expect fun isWhatsAppInstalled(): Boolean

expect fun hasSmsPermission(): Boolean
