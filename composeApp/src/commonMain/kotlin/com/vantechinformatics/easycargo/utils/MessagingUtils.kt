package com.vantechinformatics.easycargo.utils

expect fun openWhatsApp(phone: String, message: String)

expect fun openViber(phone: String, message: String)

expect fun isWhatsAppInstalled(): Boolean

expect fun isViberInstalled(): Boolean
