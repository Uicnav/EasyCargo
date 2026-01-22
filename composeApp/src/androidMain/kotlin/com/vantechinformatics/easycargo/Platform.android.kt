package com.vantechinformatics.easycargo

import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun Double.format(digits: Int): String {
    return "%.${digits}f".format(this)
}

actual fun Long.getFormattedDate(): String {
    val date = Date(this * 1000)
    val formatter = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(date)
}