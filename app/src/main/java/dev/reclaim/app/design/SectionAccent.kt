package dev.reclaim.app.design

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The identity of one tab: the wash behind it and the color its controls take
 * when they are on.
 */
data class SectionAccent(
    val soft: Color,
    val tint: Color,
    val deep: Color,
    val wash: Array<Pair<Float, Color>>,
)

val SageSection = SectionAccent(
    soft = ReclaimColors.Sage.Soft,
    tint = ReclaimColors.Sage.Tint,
    deep = ReclaimColors.Sage.Deep,
    wash = arrayOf(
        0f to ReclaimColors.Sage.Soft,
        0.45f to ReclaimColors.Sage.Soft.copy(alpha = 0.35f),
        0.78f to Color.Transparent,
    ),
)

val PeachSection = SectionAccent(
    soft = ReclaimColors.Peach.Soft,
    tint = ReclaimColors.Peach.Tint,
    deep = ReclaimColors.Peach.Deep,
    wash = arrayOf(
        0f to ReclaimColors.Butter.Soft,
        0.30f to ReclaimColors.Peach.Soft,
        0.72f to Color.Transparent,
    ),
)

val LilacSection = SectionAccent(
    soft = ReclaimColors.Lilac.Soft,
    tint = ReclaimColors.Lilac.Tint,
    deep = ReclaimColors.Lilac.Deep,
    wash = arrayOf(
        0f to ReclaimColors.Lilac.Soft,
        0.45f to ReclaimColors.Lilac.Soft.copy(alpha = 0.35f),
        0.78f to Color.Transparent,
    ),
)

val SkySection = SectionAccent(
    soft = ReclaimColors.Sky.Soft,
    tint = ReclaimColors.Sky.Tint,
    deep = ReclaimColors.Sky.Deep,
    wash = arrayOf(
        0f to ReclaimColors.Sky.Soft,
        0.45f to ReclaimColors.Sky.Soft.copy(alpha = 0.35f),
        0.78f to Color.Transparent,
    ),
)

/**
 * Controls read this, so a switch on the Reels tab is lilac and the same switch
 * on the Web tab is sky without any screen passing a color.
 */
val LocalSectionAccent = compositionLocalOf { SageSection }
