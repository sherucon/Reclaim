package dev.reclaim.app.ui.nav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.reclaim.app.design.Elevation
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.components.DockItem
import dev.reclaim.app.design.components.FloatingDock
import dev.reclaim.app.ui.apps.AppsScreen
import dev.reclaim.app.ui.home.HomeScreen
import dev.reclaim.app.ui.reels.ReelsScreen
import dev.reclaim.app.ui.webblock.WebBlockerScreen

@Composable
fun ReclaimApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val current = ReclaimDestination.fromRoute(backStackEntry?.destination?.route)

    val navigate: (ReclaimDestination) -> Unit = { destination ->
        if (destination != current) {
            navController.navigate(destination.route) {
                popUpTo(ReclaimDestination.HOME.route) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = ReclaimDestination.HOME.route,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(ReclaimDestination.HOME.route) { HomeScreen(onNavigate = navigate) }
        composable(ReclaimDestination.APPS.route) { AppsScreen(onNavigate = navigate) }
        composable(ReclaimDestination.REELS.route) { ReelsScreen(onNavigate = navigate) }
        composable(ReclaimDestination.WEB.route) { WebBlockerScreen(onNavigate = navigate) }
    }
}

@Composable
fun ReclaimDock(
    current: ReclaimDestination,
    onNavigate: (ReclaimDestination) -> Unit,
) {
    FloatingDock(
        items = ReclaimDestination.entries.map { DockItem(icon = it.icon, label = it.label) },
        selectedIndex = current.ordinal,
        onSelect = { index -> onNavigate(ReclaimDestination.entries[index]) },
        modifier = Modifier.shadow(
            elevation = Elevation.dock,
            shape = CircleShape,
            ambientColor = ReclaimColors.Shadow,
            spotColor = Color.Black.copy(alpha = 0.08f),
        ),
    )
}
