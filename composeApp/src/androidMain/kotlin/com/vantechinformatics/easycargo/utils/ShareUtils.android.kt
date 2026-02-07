package com.vantechinformatics.easycargo.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent

@SuppressLint("StaticFieldLeak")
private var appContext: Context? = null

fun initShareContext(context: Context) {
    appContext = context.applicationContext
}

actual fun shareText(text: String) {
    val ctx = appContext ?: return
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(sendIntent, null)
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    ctx.startActivity(chooser)
}
