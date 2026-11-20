package dev.reclaim.app.ui.nav

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.reclaim.app.AppContainer
import dev.reclaim.app.analytics.AnalyticsEvents
import dev.reclaim.app.design.LilacSection
import dev.reclaim.app.design.LocalSectionAccent
import dev.reclaim.app.design.PeachSection
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.SageSection
import dev.reclaim.app.design.SectionAccent
import dev.reclaim.app.design.SkySection
import dev.reclaim.app.design.Space
import dev.reclaim.app.design.components.DockItem
import dev.reclaim.app.design.components.FloatingDock
import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.ui.apps.AppsScreen
import dev.reclaim.app.ui.home.HomeScreen
import dev.reclaim.app.ui.reels.ReelsScreen
import dev.reclaim.app.ui.settings.SettingsScreen
import dev.reclaim.app.ui.webblock.WebBlockerScreen
import kotlinx.coroutines.launch

/**
 * Four screens on a horizontal pager, so the page and the dock can both be
 * dragged. The wash and every control accent follow the settled page.
 */
@Composable
fun ReclaimApp(
    container: AppContainer,
    settings: AppSettings,
) {
    var showSettings by rememberSaveable { mutableStateOf(false) }
    val destinations = ReclaimDestination.entries
    val pagerState = rememberPagerState(pageCount = { destinations.size })
    val scope = rememberCoroutineScope()
    val onUpdate: ((AppSettings) -> AppSettings) -> Unit = { transform ->
        scope.launch { container.settingsRepository.update(transform) }
    }
    // The wash waits for the page to settle, so control colours never recompose
    // while the pager is still moving.
    val accent = accentFor(destinations[pagerState.settledPage])

    CompositionLocalProvider(LocalSectionAccent provides accent) {
        Box(modifier = Modifier.fillMaxSize()) {
            ReclaimBackdrop(accent = accent)

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                // Compose the neighbour tab up front so the first swipe into it
                // does not pay for the whole screen mid-gesture.
                beyondViewportPageCount = 1,
                key = { destinations[it].route },
            ) { page ->
                val onNavigate: (ReclaimDestination) -> Unit = { target ->
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = target.ordinal,
                            animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
                        )
                    }
                }

                when (destinations[page]) {
                    ReclaimDestination.HOME -> HomeScreen(
                        settings = settings,
                        onNavigate = onNavigate,
                        onOpenSettings = { showSettings = true },
                        onUpdate = onUpdate,
                        onFeatureToggled = { feature, enabled ->
                            container.analytics.logEvent(
                                AnalyticsEvents.FEATURE_TOGGLED,
                                mapOf(
                                    AnalyticsEvents.Params.FEATURE to feature,
                                    AnalyticsEvents.Params.ENABLED to enabled,
                                ),
                            )
                        },
                    )
                    ReclaimDestination.APPS -> AppsScreen(settings = settings, onUpdate = onUpdate)
                    ReclaimDestination.REELS -> ReelsScreen(settings = settings, onUpdate = onUpdate)
                    ReclaimDestination.WEB -> WebBlockerScreen(settings = settings, onUpdate = onUpdate)
                }
            }

            // Content fades out before it reaches the glass dock, so the bar
            // shows shapes rather than readable text through it.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(168.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, ReclaimColors.Canvas.copy(alpha = 0.9f)),
                        ),
                    ),
            )

            FloatingDock(
                items = ReclaimDestination.entries.map { DockItem(icon = it.icon, label = it.label) },
                selectedIndex = pagerState.currentPage,
                onSelect = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = index,
                            animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
                        )
                    }
                },
                position = { pagerState.currentPage + pagerState.currentPageOffsetFraction },
                onScrub = { index -> scope.launch { pagerState.scrollToPage(index) } },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = Space.lg),
            )

            if (showSettings) {
                SettingsScreen(container = container, onClose = { showSettings = false })
            }
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
