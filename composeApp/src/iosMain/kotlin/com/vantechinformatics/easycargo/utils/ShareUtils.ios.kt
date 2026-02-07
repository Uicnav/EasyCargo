package com.vantechinformatics.easycargo.utils

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual fun shareText(text: String) {
    val activityController = UIActivityViewController(
        activityItems = listOf(text),
        applicationActivities = null
    )
    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    rootViewController?.presentViewController(activityController, animated = true, completion = null)
}
