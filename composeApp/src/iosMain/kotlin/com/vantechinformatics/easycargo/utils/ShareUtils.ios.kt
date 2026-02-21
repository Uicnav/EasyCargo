package com.vantechinformatics.easycargo.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIPopoverPresentationController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.UIKit.popoverPresentationController

@OptIn(ExperimentalForeignApi::class)
actual fun shareText(text: String) {
    val activityController = UIActivityViewController(
        activityItems = listOf(text),
        applicationActivities = null
    )
    val scene = UIApplication.sharedApplication.connectedScenes.firstOrNull()
    val windowScene = scene as? UIWindowScene
    val window = windowScene?.windows?.firstOrNull() as? UIWindow
    val rootViewController = window?.rootViewController
        ?: UIApplication.sharedApplication.keyWindow?.rootViewController
        ?: return

    // Required for iPad: configure popover presentation to avoid crash
    val popover: UIPopoverPresentationController? =
        activityController.popoverPresentationController
    popover?.sourceView = rootViewController.view
    popover?.sourceRect = CGRectMake(0.0, 0.0, 1.0, 1.0)
    popover?.permittedArrowDirections = 0u

    rootViewController.presentViewController(activityController, animated = true, completion = null)
}
