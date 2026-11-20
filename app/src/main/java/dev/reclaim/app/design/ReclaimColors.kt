package dev.reclaim.app.design

import androidx.compose.ui.graphics.Color

/**
 * Every color in the main app comes from here. Screens never declare raw hex.
 * The onboarding surface uses its own palette in [OnboardingColors].
 *
 * Each accent has three steps: Soft for the strongest fill, Tint for the far end
 * of a card gradient (never the canvas, so a card keeps its edge), and Deep for
 * text and icons.
 */
object ReclaimColors {
    val Canvas = Color(0xFFF6F4EF)
    val CanvasRaised = Color(0xFFEFECE4)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceMuted = Color(0xFFFBFAF7)
    val Hairline = Color(0xFFECE7DD)
    val Ink = Color(0xFF1F1B16)
    val InkSecondary = Color(0xFF6F675D)
    val InkTertiary = Color(0xFF9A9186)
    val Shadow = Color(0xFF3A3226)

    /** Warm recess for inset controls, so they read the same on canvas or a wash. */
    val Recess = Color(0x0F1F1B16)
    val RecessEdge = Color(0x141F1B16)

    object Sage {
        val Soft = Color(0xFFDDEBDD)
        val Tint = Color(0xFFF4F8F4)
        val Deep = Color(0xFF4E7A5A)
    }

    object Peach {
        val Soft = Color(0xFFF8E0D2)
        val Tint = Color(0xFFFBF6F2)
        val Deep = Color(0xFFB4643F)
    }

    object Butter {
        val Soft = Color(0xFFF7ECCB)
        val Tint = Color(0xFFFBF8F0)
        val Deep = Color(0xFF8A6A26)
    }

    object Sky {
        val Soft = Color(0xFFDCE9F7)
        val Tint = Color(0xFFF5F8FC)
        val Deep = Color(0xFF3E6E9E)
    }

    object Lilac {
        val Soft = Color(0xFFE6DFF4)
        val Tint = Color(0xFFF7F5FB)
        val Deep = Color(0xFF6B5A93)
    }

    object Blush {
        val Soft = Color(0xFFF6DDE3)
        val Tint = Color(0xFFFBF5F6)
        val Deep = Color(0xFFA3536A)
    }
}
