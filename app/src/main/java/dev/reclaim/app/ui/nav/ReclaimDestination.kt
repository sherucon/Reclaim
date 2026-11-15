package dev.reclaim.app.ui.nav

import androidx.annotation.DrawableRes
import dev.reclaim.app.design.icons.ReclaimIcons

enum class ReclaimDestination(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int,
) {
    HOME("home", "Home", ReclaimIcons.Home),
    APPS("apps", "Apps", ReclaimIcons.Apps),
    REELS("reels", "Reels", ReclaimIcons.Reels),
    WEB("web", "Web", ReclaimIcons.Web),
    ;

    companion object {
        fun fromRoute(route: String?): ReclaimDestination = entries.firstOrNull { it.route == route } ?: HOME
    }
}
