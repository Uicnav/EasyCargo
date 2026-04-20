package com.vantechinformatics.easycargo.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.StoreKit.SKStoreReviewController
import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIWindowScene

private const val APP_STORE_ID = "6758101546"

@OptIn(ExperimentalForeignApi::class)
actual fun requestAppReview() {
    val scene = foregroundWindowScene() ?: return
    SKStoreReviewController.requestReviewInScene(scene)
}

actual fun openStorePage() {
    val url = NSURL.URLWithString(
        "https://apps.apple.com/app/id$APP_STORE_ID?action=write-review"
    ) ?: return
    UIApplication.sharedApplication.openURL(url)
}

@OptIn(ExperimentalForeignApi::class)
private fun foregroundWindowScene(): UIWindowScene? {
    val active = UIApplication.sharedApplication.connectedScenes.firstOrNull { scene ->
        (scene as? UIWindowScene)?.activationState == UISceneActivationStateForegroundActive
    } as? UIWindowScene
    if (active != null) return active
    return UIApplication.sharedApplication.connectedScenes.firstOrNull() as? UIWindowScene
}
