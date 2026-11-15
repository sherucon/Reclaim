package dev.reclaim.app.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import dev.reclaim.app.design.components.MiniBarChart
import dev.reclaim.app.design.components.PillToggle
import dev.reclaim.app.design.components.SegmentedPills
import dev.reclaim.app.design.components.SoftCard
import dev.reclaim.app.design.components.StatusRing
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Goldens for the five components that carry the visual identity. Record with
 * `./gradlew recordRoborazziPlayDebug`, verify with
 * `./gradlew verifyRoborazziPlayDebug`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xhdpi")
class ComponentScreenshotTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun softCard() =
        capture("soft_card") {
            SoftCard {
                Text(
                    text = "Protection",
                    style = MaterialTheme.typography.labelMedium,
                    color = ReclaimColors.InkSecondary,
                )
                Text(
                    text = "Almost there",
                    style = MaterialTheme.typography.titleLarge,
                    color = ReclaimColors.Ink,
                )
                Text(
                    text = "One more switch and the whole day is covered.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReclaimColors.InkSecondary,
                )
            }
        }

    @Test
    fun pillToggle() =
        capture("pill_toggle") {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                PillToggle(checked = true, onCheckedChange = {}, contentDescription = "On")
                PillToggle(checked = false, onCheckedChange = {}, contentDescription = "Off")
            }
        }

    @Test
    fun segmentedPills() =
        capture("segmented_pills") {
            SegmentedPills(
                options = listOf("Gentle", "Balanced", "Strict"),
                selected = "Balanced",
                onSelect = {},
                label = { it },
            )
        }

    @Test
    fun statusRing() =
        capture("status_ring") {
            StatusRing(progress = 2f / 3f, progressColor = ReclaimColors.Sage.Deep) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "2 of 3",
                        style = MaterialTheme.typography.titleMedium,
                        color = ReclaimColors.Ink,
                    )
                    Text(
                        text = "on",
                        style = MaterialTheme.typography.labelSmall,
                        color = ReclaimColors.InkTertiary,
                    )
                }
            }
        }

    @Test
    fun miniBarChart() =
        capture("mini_bar_chart") {
            MiniBarChart(
                values = listOf(1, 4, 2, 6, 3, 5, 2),
                highlightIndex = 6,
            )
        }

    private fun capture(
        name: String,
        content: @androidx.compose.runtime.Composable () -> Unit,
    ) {
        compose.setContent {
            ReclaimTheme {
                Box(
                    modifier = Modifier
                        .size(width = 360.dp, height = 200.dp)
                        .background(ReclaimColors.Canvas)
                        .padding(20.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    content()
                }
            }
        }
        compose.mainClock.advanceTimeBy(1_000)
        // Linux and macOS antialias arcs and text a little differently, so a
        // small share of edge pixels is allowed to move.
        compose.onRoot().captureRoboImage(
            filePath = "src/test/screenshots/$name.png",
            roborazziOptions = RoborazziOptions(
                compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0.02f),
            ),
        )
    }
}
