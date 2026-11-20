package dev.reclaim.app.ui.webblock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.ReclaimTheme
import dev.reclaim.app.design.SkySection
import dev.reclaim.app.design.Space
import dev.reclaim.app.design.components.IconBubble
import dev.reclaim.app.design.components.MiniBarChart
import dev.reclaim.app.design.components.PillToggle
import dev.reclaim.app.design.components.ScreenScaffold
import dev.reclaim.app.design.components.SoftCard
import dev.reclaim.app.design.icons.ReclaimIcons
import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.domain.model.DomainCategory
import dev.reclaim.app.ui.common.SectionPreview

private val DemoQueries = listOf(12, 28, 21, 44, 39, 52, 34)

@Composable
fun WebBlockerScreen(
    settings: AppSettings,
    onUpdate: ((AppSettings) -> AppSettings) -> Unit,
    modifier: Modifier = Modifier,
) {
    val webOn = settings.webBlockingEnabled
    val categories = settings.webCategories

    ScreenScaffold(
        modifier = modifier,
        title = "Web",
        subtitle = "Quiet the mobile web, in every browser on the phone.",
    ) {
        FilterCard(
            enabled = webOn,
            onEnabledChange = { enabled -> onUpdate { it.copy(webBlockingEnabled = enabled) } },
        )

        Spacer(Modifier.height(Space.cardGap))

        CategoryCard(
            icon = ReclaimIcons.Users,
            title = "Social",
            detail = "1,180 sites",
            tint = ReclaimColors.Peach.Deep,
            background = ReclaimColors.Peach.Soft,
            checked = DomainCategory.SOCIAL in categories,
            onCheckedChange = { on ->
                onUpdate { it.copy(webCategories = it.webCategories.toggle(DomainCategory.SOCIAL, on)) }
            },
        )
        Spacer(Modifier.height(Space.cardGap))
        CategoryCard(
            icon = ReclaimIcons.ShieldAlert,
            title = "Adult",
            detail = "42 sites",
            tint = ReclaimColors.Blush.Deep,
            background = ReclaimColors.Blush.Soft,
            checked = DomainCategory.ADULT in categories,
            onCheckedChange = { on ->
                onUpdate { it.copy(webCategories = it.webCategories.toggle(DomainCategory.ADULT, on)) }
            },
        )
        Spacer(Modifier.height(Space.cardGap))
        CategoryCard(
            icon = ReclaimIcons.Banknotes,
            title = "Gambling",
            detail = "18 sites",
            tint = ReclaimColors.Lilac.Deep,
            background = ReclaimColors.Lilac.Soft,
            checked = DomainCategory.GAMBLING in categories,
            onCheckedChange = { on ->
                onUpdate { it.copy(webCategories = it.webCategories.toggle(DomainCategory.GAMBLING, on)) }
            },
        )

        Spacer(Modifier.height(Space.cardGap))

        BlockedQueriesCard()

        Spacer(Modifier.height(Space.cardGap))

        SoftCard {
            Row(verticalAlignment = Alignment.Top) {
                IconBubble(
                    icon = ReclaimIcons.Info,
                    tint = ReclaimColors.Sky.Deep,
                    background = ReclaimColors.Sky.Soft,
                )
                Spacer(Modifier.width(Space.md))
                Column {
                    Text(
                        text = "How it works",
                        style = MaterialTheme.typography.titleMedium,
                        color = ReclaimColors.Ink,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Reclaim blocks the sites you choose in every browser on the phone. " +
                            "A few apps ignore the phone's settings and get around it.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ReclaimColors.InkSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterCard(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
) {
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble(
                icon = ReclaimIcons.Shield,
                tint = ReclaimColors.Sky.Deep,
                background = ReclaimColors.Sky.Soft,
                size = 44.dp,
                iconSize = 22.dp,
            )
            Spacer(Modifier.width(Space.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Web filter",
                    style = MaterialTheme.typography.titleMedium,
                    color = ReclaimColors.Ink,
                )
                Text(
                    text = if (enabled) "1,240 sites blocked" else "The filter is off",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReclaimColors.InkSecondary,
                )
            }
            PillToggle(
                checked = enabled,
                onCheckedChange = onEnabledChange,
                contentDescription = "Web filter",
            )
        }
    }
}

@Composable
private fun CategoryCard(
    icon: Int,
    title: String,
    detail: String,
    tint: androidx.compose.ui.graphics.Color,
    background: androidx.compose.ui.graphics.Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    SoftCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconBubble(icon = icon, tint = tint, background = background)
            Spacer(Modifier.width(Space.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = ReclaimColors.Ink,
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReclaimColors.InkSecondary,
                )
            }
            PillToggle(
                checked = checked,
                onCheckedChange = onCheckedChange,
                contentDescription = title,
            )
        }
    }
}

private fun Set<DomainCategory>.toggle(
    category: DomainCategory,
    enabled: Boolean,
): Set<DomainCategory> = if (enabled) this + category else this - category

@Composable
private fun BlockedQueriesCard() {
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Blocked today",
                    style = MaterialTheme.typography.labelMedium,
                    color = ReclaimColors.InkTertiary,
                )
                Spacer(Modifier.height(Space.xs))
                Text(
                    text = "${DemoQueries.last()}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = ReclaimColors.Ink,
                )
            }
            MiniBarChart(
                values = DemoQueries,
                highlightIndex = DemoQueries.lastIndex,
                barColor = ReclaimColors.Sky.Deep.copy(alpha = 0.25f),
                highlightColor = ReclaimColors.Sky.Deep,
                height = 48.dp,
                barWidth = 8.dp,
                contentDescription = "Blocked queries on each of the last seven days",
            )
        }
        Spacer(Modifier.height(Space.md))
        Row(horizontalArrangement = Arrangement.spacedBy(Space.sm)) {
            Text(
                text = "Counted on the phone. No site name ever leaves it.",
                style = MaterialTheme.typography.labelSmall,
                color = ReclaimColors.InkTertiary,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F4EF)
@Composable
private fun WebPreview() {
    ReclaimTheme {
        SectionPreview(SkySection) {
            WebBlockerScreen(settings = AppSettings(), onUpdate = {})
        }
    }
}
