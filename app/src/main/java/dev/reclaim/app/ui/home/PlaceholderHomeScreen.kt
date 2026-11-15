package dev.reclaim.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.ReclaimTheme
import dev.reclaim.app.design.Space
import dev.reclaim.app.design.components.SoftCard
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Phase 0 stand-in for Home. It exists to prove the tokens, the font, and the
 * shell render on a device; the real Home lands in Phase 2.
 */
@Composable
fun PlaceholderHomeScreen(modifier: Modifier = Modifier) {
    val locale = Locale.getDefault()
    val today =
        remember {
            LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))
        }
    val greeting =
        remember {
            when (LocalTime.now().hour) {
                in 0..11 -> "Good morning"
                in 12..17 -> "Good afternoon"
                else -> "Good evening"
            }
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(ReclaimColors.Canvas)
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Space.screenHorizontal),
    ) {
        Spacer(Modifier.height(Space.xxl))

        Text(
            text = today.uppercase(locale),
            style = MaterialTheme.typography.labelSmall,
            color = ReclaimColors.InkTertiary,
        )
        Spacer(Modifier.height(Space.sm))
        Text(
            text = greeting,
            style = MaterialTheme.typography.displayLarge,
            color = ReclaimColors.Ink,
        )
        Spacer(Modifier.height(Space.xs))
        Text(
            text = "Reclaim is getting ready.",
            style = MaterialTheme.typography.bodyLarge,
            color = ReclaimColors.InkSecondary,
        )

        Spacer(Modifier.height(Space.section))

        SoftCard {
            Text(
                text = "PHASE 0",
                style = MaterialTheme.typography.labelSmall,
                color = ReclaimColors.InkTertiary,
            )
            Spacer(Modifier.height(Space.sm))
            Text(
                text = "The shell is alive.",
                style = MaterialTheme.typography.titleLarge,
                color = ReclaimColors.Ink,
            )
            Spacer(Modifier.height(Space.sm))
            Text(
                text =
                    "Design tokens, two build flavors, and telemetry are wired. " +
                        "Next up: onboarding, then blocking.",
                style = MaterialTheme.typography.bodyMedium,
                color = ReclaimColors.InkSecondary,
            )
            Spacer(Modifier.height(Space.lg))
            Row(horizontalArrangement = Arrangement.spacedBy(Space.sm)) {
                StatChip("Compose")
                StatChip("Play + Libre")
            }
        }

        Spacer(Modifier.height(Space.xxl))
        Text(
            text = "Build 0.1.0",
            style = MaterialTheme.typography.labelSmall,
            color = ReclaimColors.InkTertiary,
        )
        Spacer(Modifier.height(Space.huge))
    }
}

@Composable
private fun StatChip(label: String) {
    Surface(
        shape = CircleShape,
        color = ReclaimColors.SurfaceMuted,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = Space.md, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = ReclaimColors.InkSecondary,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F4EF)
@Composable
private fun PlaceholderHomePreview() {
    ReclaimTheme {
        PlaceholderHomeScreen()
    }
}
