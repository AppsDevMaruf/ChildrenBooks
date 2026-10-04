package com.childrenbooks.core.platform

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.net.Uri
import com.childrenbooks.R

/** Every link that leaves the app. All of them are reachable only behind the parent gate. */
object AppLinks {
    const val MoreApps = "https://play.google.com/store/apps/developer?id=Maruf+Alam"
    const val PrivacyPolicy =
        "https://childrenbooksprivacypolicy.blogspot.com/2026/07/children-books-privacy-policy-effective.html"

    fun storePage(packageName: String): String = "https://play.google.com/store/apps/details?id=$packageName"
}

fun Context.openUri(uri: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, getString(R.string.toast_no_compatible_app), Toast.LENGTH_SHORT).show()
    }
}

fun Context.shareText(text: String, chooserTitle: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    try {
        startActivity(Intent.createChooser(send, chooserTitle))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, getString(R.string.toast_no_compatible_app), Toast.LENGTH_SHORT).show()
    }
}

fun Context.appVersionName(): String =
    runCatching { packageManager.getPackageInfo(packageName, 0).versionName }.getOrNull().orEmpty()
