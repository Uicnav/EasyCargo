package com.vantechinformatics.easycargo.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private var reviewActivity: Activity? = null
private val reviewScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

fun initReviewContext(activity: Activity) {
    reviewActivity = activity
}

fun clearReviewContext() {
    reviewActivity = null
}

actual fun requestAppReview() {
    val activity = reviewActivity ?: return
    val manager = ReviewManagerFactory.create(activity)
    reviewScope.launch {
        try {
            val reviewInfo = manager.requestReviewFlow().await()
            manager.launchReviewFlow(activity, reviewInfo).await()
        } catch (_: Exception) {
            // Play throttles and can fail silently; never surface this to the user.
        }
    }
}

actual fun openStorePage() {
    val activity = reviewActivity ?: return
    val pkg = activity.packageName
    val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        activity.startActivity(market)
    } catch (_: Exception) {
        val web = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
        ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        activity.startActivity(web)
    }
}
