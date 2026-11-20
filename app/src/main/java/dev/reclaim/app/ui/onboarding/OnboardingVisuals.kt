package dev.reclaim.app.ui.onboarding

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import dev.reclaim.app.R
import dev.reclaim.app.design.OnboardingColors
import dev.reclaim.app.design.OnboardingShapes
import dev.reclaim.app.design.OnboardingSpace
import dev.reclaim.app.design.OnboardingType
import dev.reclaim.app.design.icons.ReclaimBrandIcon
import dev.reclaim.app.design.icons.ReclaimBrandIcons
import dev.reclaim.app.design.icons.ReclaimIcon
import dev.reclaim.app.design.icons.ReclaimIcons
import dev.reclaim.app.domain.model.Platform

@Composable
internal fun OnboardingBackdrop() {
    Box(modifier = Modifier.fillMaxSize().background(OnboardingColors.Paper)) {
        Image(
            painter = painterResource(R.drawable.onboarding_orange),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

/**
 * The design is light type over a saturated gradient, so the status bar icons
 * turn light while onboarding is on screen and go back to the app's dark-on-
 * light style when the flow finishes. The navigation bar stays dark: the
 * gradient fades to a pale orange behind it.
 */
@Composable
internal fun OnboardingLightSystemBars() {
    val view = LocalView.current
    val activity = LocalActivity.current
    DisposableEffect(view, activity) {
        val controller = activity?.let { WindowCompat.getInsetsController(it.window, view) }
        val previousStatus = controller?.isAppearanceLightStatusBars
        controller?.isAppearanceLightStatusBars = false
        onDispose {
            controller?.let { bars ->
                previousStatus?.let { bars.isAppearanceLightStatusBars = it }
            }
        }
    }
}

/**
 * Fades the edges of a scroll viewport into whatever is behind it. The mask is
 * fixed to the viewport, so content fades as it passes under the edge.
 */
internal fun Modifier.verticalEdgeFade(
    top: Dp,
    bottom: Dp,
): Modifier =
    graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            if (size.height <= 0f) return@drawWithContent
            val topStop = (top.toPx() / size.height).coerceIn(0f, 0.5f)
            val bottomStop = 1f - (bottom.toPx() / size.height).coerceIn(0f, 0.5f)
            drawRect(
                brush = Brush.verticalGradient(
                    0f to Color.Transparent,
                    topStop to Color.Black,
                    bottomStop to Color.Black,
                    1f to Color.Transparent,
                ),
                blendMode = BlendMode.DstIn,
            )
        }

@Composable
internal fun OnboardingWordmark(modifier: Modifier = Modifier) {
    Text(
        text = "Reclaim",
        style = OnboardingType.Wordmark,
        color = OnboardingColors.Paper,
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
internal fun OnboardingBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(OnboardingColors.Ink),
    ) {
        ReclaimIcon(
            icon = ReclaimIcons.Back,
            contentDescription = "Back",
            size = 18.dp,
            tint = OnboardingColors.Paper,
        )
    }
}

@Composable
internal fun OnboardingPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(66.67.dp),
        shape = CircleShape,
        color = OnboardingColors.Ink,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = OnboardingType.Body,
                color = OnboardingColors.Paper,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
internal fun OnboardingStepHeader(
    title: String,
    helper: String? = null,
    minHeight: Dp = 145.67.dp,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = OnboardingSpace.Text)
            .heightIn(min = minHeight),
    ) {
        Text(
            text = title,
            style = OnboardingType.Title,
            color = OnboardingColors.Paper,
        )
        if (helper != null) {
            Spacer(Modifier.height(6.67.dp))
            Text(
                text = helper,
                style = OnboardingType.Body,
                color = OnboardingColors.Paper,
            )
        }
    }
}

@Composable
internal fun OnboardingChoice(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    leading: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    detail: String? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .toggleable(
                value = selected,
                role = Role.Checkbox,
                onValueChange = { onClick() },
            ),
        shape = CircleShape,
        color = OnboardingColors.Paper,
        border = BorderStroke(1.5.dp, OnboardingColors.Ink),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 66.67.dp)
                .padding(start = 24.dp, top = 10.dp, end = 26.67.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leading()
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = OnboardingType.Body,
                    color = OnboardingColors.Ink,
                )
                if (detail != null) {
                    Text(
                        text = detail,
                        style = OnboardingType.Body,
                        color = OnboardingColors.Muted,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            OnboardingCheckbox(checked = selected)
        }
    }
}

@Composable
private fun OnboardingCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = OnboardingShapes.Checkbox
    Box(
        modifier = modifier
            .size(25.dp)
            .clip(shape)
            .background(OnboardingColors.Paper)
            .border(1.5.dp, OnboardingColors.Ink, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            ReclaimIcon(
                icon = ReclaimIcons.Check,
                contentDescription = null,
                size = 16.dp,
                tint = OnboardingColors.Ink,
            )
        }
    }
}

@Composable
internal fun OnboardingPlatformMark(
    platform: Platform,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(25.dp),
        contentAlignment = Alignment.Center,
    ) {
        ReclaimBrandIcon(
            icon = brandIconFor(platform),
            contentDescription = null,
            size = 20.dp,
            tint = OnboardingColors.Ink,
        )
    }
}

private fun brandIconFor(platform: Platform): Int =
    when (platform) {
        Platform.INSTAGRAM -> ReclaimBrandIcons.Instagram
        Platform.YOUTUBE -> ReclaimBrandIcons.YouTube
        Platform.TIKTOK -> ReclaimBrandIcons.TikTok
        Platform.FACEBOOK -> ReclaimBrandIcons.Facebook
        Platform.SNAPCHAT -> ReclaimBrandIcons.Snapchat
        Platform.X -> ReclaimBrandIcons.X
        Platform.LINKEDIN -> ReclaimBrandIcons.LinkedIn
        Platform.REDDIT -> ReclaimBrandIcons.Reddit
    }

@Composable
internal fun OnboardingGlyph(
    icon: Int,
    modifier: Modifier = Modifier,
    size: Dp = 25.dp,
) {
    ReclaimIcon(
        icon = icon,
        contentDescription = null,
        size = size,
        tint = OnboardingColors.Ink,
        modifier = modifier,
    )
}

@Composable
internal fun OnboardingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth().height(66.67.dp),
        shape = CircleShape,
        color = OnboardingColors.Paper,
        border = BorderStroke(OnboardingShapes.ActionBorder, OnboardingColors.Ink),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            singleLine = true,
            textStyle = OnboardingType.Body.copy(
                color = OnboardingColors.Ink,
                textAlign = TextAlign.Center,
            ),
            cursorBrush = SolidColor(OnboardingColors.Ink),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done,
            ),
            decorationBox = { inner ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (value.isEmpty()) {
                        Text(
                            text = "John Doe",
                            style = OnboardingType.Body.copy(textAlign = TextAlign.Center),
                            color = OnboardingColors.Placeholder,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    inner()
                }
            },
        )
    }
}

@Composable
internal fun OnboardingPermissionCard(
    description: String,
    granted: Boolean,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth().heightIn(min = 180.dp),
        shape = OnboardingShapes.Card,
        color = OnboardingColors.Paper,
        border = BorderStroke(1.5.dp, OnboardingColors.Ink),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.padding(start = 7.dp, end = 2.dp),
                verticalAlignment = Alignment.Top,
            ) {
                OnboardingGlyph(
                    icon = ReclaimIcons.Shield,
                    modifier = Modifier.padding(top = 11.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Access",
                        style = OnboardingType.Body,
                        color = OnboardingColors.Ink,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = description,
                        style = OnboardingType.Body,
                        color = OnboardingColors.Ink,
                    )
                }
                Spacer(Modifier.width(12.dp))
                OnboardingStatusPill(granted = granted, modifier = Modifier.padding(top = 11.dp))
            }
            Spacer(Modifier.height(16.dp))
            Surface(
                onClick = onAction,
                modifier = Modifier.fillMaxWidth().height(66.67.dp),
                shape = CircleShape,
                color = OnboardingColors.Paper,
                border = BorderStroke(OnboardingShapes.ActionBorder, OnboardingColors.Ink),
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = actionLabel,
                        style = OnboardingType.Body,
                        color = OnboardingColors.Ink,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingStatusPill(
    granted: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .widthIn(min = 52.dp)
            .heightIn(min = 24.67.dp)
            .clip(CircleShape)
            .background(OnboardingColors.Paper)
            .border(1.5.dp, OnboardingColors.Ink, CircleShape)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(if (granted) OnboardingColors.Ink else OnboardingColors.Muted),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = if (granted) "On" else "Off",
            style = OnboardingType.Caption,
            color = OnboardingColors.Ink,
        )
    }
}

@Composable
internal fun OnboardingSummaryCard(
    lines: List<String>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth().heightIn(min = 66.67.dp),
        shape = OnboardingShapes.Card,
        color = OnboardingColors.Paper,
        border = BorderStroke(1.5.dp, OnboardingColors.Ink),
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            lines.forEachIndexed { index, line ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ReclaimIcon(
                        icon = ReclaimIcons.Check,
                        contentDescription = null,
                        size = 18.dp,
                        tint = OnboardingColors.Ink,
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = line,
                        style = OnboardingType.Body,
                        color = OnboardingColors.Ink,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (index != lines.lastIndex) {
                    Spacer(Modifier.height(18.dp))
                }
            }
        }
    }
}
