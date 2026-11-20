package dev.reclaim.app.ui.overlay

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.reclaim.app.MainActivity
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.ReclaimTheme
import dev.reclaim.app.design.Space
import dev.reclaim.app.design.components.PillButton
import dev.reclaim.app.design.components.PillStyle
import dev.reclaim.app.design.components.SoftCard
import dev.reclaim.app.domain.model.BlockReason

class BlockOverlayActivity : ComponentActivity() {
    private val args = mutableStateOf<BlockOverlayArgs?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )

        // The overlay is the message. Back out of it and the feed is still there.
        onBackPressedDispatcher.addCallback(this) { }

        args.value = BlockOverlayArgs.from(intent)
        setContent {
            ReclaimTheme {
                args.value?.let { overlay ->
                    BlockOverlayScreen(
                        args = overlay,
                        onGoHome = ::goHome,
                        onOpenReclaim = ::openReclaim,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        args.value = BlockOverlayArgs.from(intent)
    }

    private fun goHome() {
        startActivity(
            Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
        finish()
    }

    private fun openReclaim() {
        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
        )
        finish()
    }

    companion object {
        const val EXTRA_PACKAGE = "package"
        const val EXTRA_REASON = "reason"
    }
}

data class BlockOverlayArgs(
    val packageName: String,
    val reason: BlockReason,
) {
    companion object {
        fun from(intent: Intent): BlockOverlayArgs? {
            val packageName = intent.getStringExtra(BlockOverlayActivity.EXTRA_PACKAGE) ?: return null
            val reason = intent
                .getStringExtra(BlockOverlayActivity.EXTRA_REASON)
                ?.let { runCatching { BlockReason.valueOf(it) }.getOrNull() }
                ?: BlockReason.ALWAYS
            return BlockOverlayArgs(packageName, reason)
        }
    }
}

@Composable
private fun BlockOverlayScreen(
    args: BlockOverlayArgs,
    onGoHome: () -> Unit,
    onOpenReclaim: () -> Unit,
) {
    val context = LocalContext.current
    val label = remember(args.packageName) { appLabel(context, args.packageName) }
    val initial = remember(label) { label.take(1).uppercase() }
    val isDowntime = args.reason == BlockReason.DOWNTIME

    val brush = if (isDowntime) {
        Brush.verticalGradient(
            listOf(ReclaimColors.Butter.Soft, ReclaimColors.Peach.Soft, ReclaimColors.Peach.Tint),
        )
    } else {
        Brush.verticalGradient(
            listOf(ReclaimColors.Peach.Soft, ReclaimColors.Blush.Soft, ReclaimColors.Lilac.Tint),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(ReclaimColors.Surface.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (isDowntime) "Quiet" else initial,
                    style = MaterialTheme.typography.headlineMedium,
                    color = ReclaimColors.InkSecondary,
                )
            }

            Spacer(Modifier.height(Space.section))

            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isDowntime) "Quiet hours" else "Blocked by Reclaim",
                    style = MaterialTheme.typography.labelSmall,
                    color = ReclaimColors.InkTertiary,
                )
                Spacer(Modifier.height(Space.sm))
                Text(
                    text = if (isDowntime) "$label is resting" else "$label is blocked",
                    style = MaterialTheme.typography.headlineLarge,
                    color = ReclaimColors.Ink,
                )
                Spacer(Modifier.height(Space.sm))
                Text(
                    text = if (isDowntime) {
                        "Quiet hours are on. This app comes back when the window ends."
                    } else {
                        "You asked Reclaim to keep this one out of reach."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = ReclaimColors.InkSecondary,
                )
                Spacer(Modifier.height(Space.xxl))
                PillButton(
                    text = "Go home",
                    onClick = onGoHome,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(Space.sm))
                PillButton(
                    text = "Open Reclaim",
                    onClick = onOpenReclaim,
                    modifier = Modifier.fillMaxWidth(),
                    style = PillStyle.Secondary,
                )
            }
        }
    }
}

private fun appLabel(
    context: Context,
    packageName: String,
): String =
    runCatching {
        val info = context.packageManager.getApplicationInfo(packageName, 0)
        context.packageManager.getApplicationLabel(info).toString()
    }.getOrDefault(packageName.substringAfterLast('.'))
