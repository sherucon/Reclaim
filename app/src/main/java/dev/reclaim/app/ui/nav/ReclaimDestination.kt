package dev.reclaim.app.ui.nav

import dev.reclaim.app.design.icons.ReclaimIcons

enum class ReclaimDestination(
    val route: String,
    val label: String,
    val icon: Int,
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
