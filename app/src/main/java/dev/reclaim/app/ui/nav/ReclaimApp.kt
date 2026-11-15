package dev.reclaim.app.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import dev.reclaim.app.design.Elevation
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.Space
import dev.reclaim.app.design.components.DockItem
import dev.reclaim.app.design.components.FloatingDock
import dev.reclaim.app.ui.apps.AppsScreen
import dev.reclaim.app.ui.home.HomeScreen
import dev.reclaim.app.ui.reels.ReelsScreen
import dev.reclaim.app.ui.webblock.WebBlockerScreen
import kotlinx.coroutines.launch

/**
 * Four screens on a horizontal pager, so the page and the dock can both be
 * dragged. The section wash behind them follows the settled page.
 */
@Composable
fun ReclaimApp() {
    val destinations = ReclaimDestination.entries
    val pagerState = rememberPagerState(pageCount = { destinations.size })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        ReclaimBackdrop(destination = destinations[pagerState.currentPage])

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { destinations[it].route },
        ) { page ->
            val onNavigate: (ReclaimDestination) -> Unit = { target ->
                scope.launch { pagerState.animateScrollToPage(target.ordinal) }
            }

            when (destinations[page]) {
                ReclaimDestination.HOME -> HomeScreen(onNavigate = onNavigate)
                ReclaimDestination.APPS -> AppsScreen()
                ReclaimDestination.REELS -> ReelsScreen()
                ReclaimDestination.WEB -> WebBlockerScreen()
            }
        }

        ReclaimDock(
            currentIndex = pagerState.currentPage,
            onSelect = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
            onScrub = { index -> scope.launch { pagerState.scrollToPage(index) } },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = Space.lg)
                .shadow(
                    elevation = Elevation.dock,
                    shape = CircleShape,
                    ambientColor = ReclaimColors.Shadow,
                    spotColor = Color.Black.copy(alpha = 0.08f),
                ),
        )
    }
}

@Composable
private fun ReclaimDock(
    currentIndex: Int,
    onSelect: (Int) -> Unit,
    onScrub: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingDock(
        items = ReclaimDestination.entries.map { DockItem(icon = it.icon, label = it.label) },
        selectedIndex = currentIndex,
        onSelect = onSelect,
        onScrub = onScrub,
        modifier = modifier,
    )
}
