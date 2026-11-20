package dev.reclaim.app.service.blocking

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings

/**
 * The Android-facing half of blocking: which packages must never be touched, and
 * what a decision turns into.
 */
object BlockTargets {
    fun launcherPackages(context: Context): Set<String> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val launcher = context.packageManager
            .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            ?.activityInfo
            ?.packageName
        return launcher?.let(::setOf) ?: emptySet()
    }

    fun inputMethodPackage(context: Context): String? {
        val component = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD,
        ) ?: return null
        return runCatching { ComponentName.unflattenFromString(component)?.packageName }.getOrNull()
    }

    /** Everything a decision must survive before it can become a screen. */
    fun ignoredPackages(context: Context): Set<String> =
        buildSet {
            addAll(launcherPackages(context))
            inputMethodPackage(context)?.let(::add)
        }
}
