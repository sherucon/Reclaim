package dev.reclaim.app.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.LocalSectionAccent
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.icons.ReclaimIcons

/**
 * The selection control used in onboarding and app rows. The circle is 28 dp,
 * the touch target is 48 dp.
 */
@Composable
fun CircularCheck(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    size: Dp = 28.dp,
    contentDescription: String? = null,
) {
    val accent = LocalSectionAccent.current
    val fill by animateColorAsState(
        targetValue = if (checked) accent.deep else Color.Transparent,
        animationSpec = tween(durationMillis = 140),
        label = "checkFill",
    )
    val ring by animateColorAsState(
        targetValue = if (checked) accent.deep else ReclaimColors.Hairline,
        animationSpec = tween(durationMillis = 140),
        label = "checkRing",
    )
    val checkAlpha by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 140),
        label = "checkAlpha",
    )

    val interactive = if (onClick != null) {
        Modifier.toggleable(value = checked, role = Role.Checkbox, onValueChange = { onClick() })
    } else {
        Modifier.semantics { if (contentDescription != null) this.contentDescription = contentDescription }
    }

    Box(
        modifier = modifier
            .size(48.dp)
            .then(interactive),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(fill)
                .border(width = 1.5.dp, color = ring, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (checkAlpha > 0f) {
                Icon(
                    painter = painterResource(ReclaimIcons.Check),
                    contentDescription = null,
                    modifier = Modifier.size(size * 0.58f),
                    tint = Color.White.copy(alpha = checkAlpha),
                )
            }
        }
    }
}
