package dev.reclaim.app.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.reclaim.app.design.LilacSection
import dev.reclaim.app.design.LocalSectionAccent
import dev.reclaim.app.design.PeachSection
import dev.reclaim.app.design.SageSection
import dev.reclaim.app.design.SectionAccent
import dev.reclaim.app.design.SkySection
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
 * dragged. The wash and every control accent follow the settled page.
 */
@Composable
fun ReclaimApp() {
    val destinations = ReclaimDestination.entries
    val pagerState = rememberPagerState(pageCount = { destinations.size })
    val scope = rememberCoroutineScope()
    val accent = accentFor(destinations[pagerState.currentPage])

    CompositionLocalProvider(LocalSectionAccent provides accent) {
        Box(modifier = Modifier.fillMaxSize()) {
            ReclaimBackdrop(accent = accent)

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

            FloatingDock(
                items = ReclaimDestination.entries.map { DockItem(icon = it.icon, label = it.label) },
                selectedIndex = pagerState.currentPage,
                onSelect = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
                onScrub = { index -> scope.launch { pagerState.scrollToPage(index) } },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = Space.lg),
            )
        }
    }
}

private fun accentFor(destination: ReclaimDestination): SectionAccent =
    when (destination) {
        ReclaimDestination.HOME -> SageSection
        ReclaimDestination.APPS -> PeachSection
        ReclaimDestination.REELS -> LilacSection
        ReclaimDestination.WEB -> SkySection
    }
