package dev.reclaim.app.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors

/**
 * Two or three options in a shallow recess. The selection is a raised white
 * pill, and it can be dragged across the options as well as tapped.
 */
@Composable
fun <T> SegmentedPills(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
) {
    var innerWidth by remember { mutableIntStateOf(0) }
    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(ReclaimColors.Recess)
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(ReclaimColors.RecessEdge, Color.Transparent),
                        startY = 0f,
                        endY = size.height * 0.32f,
                    ),
                    size = Size(size.width, size.height * 0.32f),
                )
            }.padding(4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { innerWidth = it.width }
                .pointerInput(options, innerWidth) {
                    if (innerWidth <= 0) return@pointerInput
                    val segment = innerWidth.toFloat() / options.size

                    fun indexAt(x: Float): Int = (x / segment).toInt().coerceIn(0, options.lastIndex)

                    detectHorizontalDragGestures(
                        onDragStart = { offset -> onSelect(options[indexAt(offset.x)]) },
                        onHorizontalDrag = { change, _ ->
                            onSelect(options[indexAt(change.position.x)])
                        },
                    )
                },
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                val background by animateColorAsState(
                    targetValue = if (isSelected) ReclaimColors.Surface else Color.Transparent,
                    animationSpec = tween(durationMillis = 160),
                    label = "segmentBackground",
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) ReclaimColors.Ink else ReclaimColors.InkSecondary,
                    animationSpec = tween(durationMillis = 160),
                    label = "segmentText",
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .shadow(
                            elevation = if (isSelected) 2.dp else 0.dp,
                            shape = CircleShape,
                        ).clip(CircleShape)
                        .background(background)
                        .selectable(
                            selected = isSelected,
                            interactionSource = interaction,
                            indication = null,
                            role = Role.Tab,
                            onClick = { onSelect(option) },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label(option),
                        style = MaterialTheme.typography.labelMedium,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
