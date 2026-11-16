package dev.reclaim.app.design.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.reclaim.app.R

/**
 * Font Awesome Free 6.7.2, Solid. One icon set, one font, everywhere.
 * Glyph names are noted beside each codepoint so a mapping is never guessed.
 * Font: SIL OFL 1.1, icons: CC BY 4.0; see docs/licenses/FontAwesome-Free.txt.
 */
object ReclaimIcons {
    val Home = 0xf015 // house
    val Apps = 0xf009 // table-cells-large
    val Reels = 0xf008 // film
    val Web = 0xf0ac // globe
    val Settings = 0xf013 // gear
    val Check = 0xf00c // check
    val ChevronRight = 0xf054 // chevron-right
    val Back = 0xf053 // chevron-left
    val Shield = 0xf132 // shield
    val ShieldAlert = 0xf071 // triangle-exclamation
    val Moon = 0xf186 // moon
    val Sun = 0xf185 // sun
    val Clock = 0xf017 // clock
    val Bell = 0xf0f3 // bell
    val Plus = 0x002b // plus
    val Search = 0xf002 // magnifying-glass
    val Info = 0xf05a // circle-info
    val Refresh = 0xf021 // arrows-rotate
    val More = 0xf141 // ellipsis
    val Close = 0xf00d // xmark
    val Users = 0xf0c0 // users
    val Banknotes = 0xf0d6 // money-bill
}

private val FontAwesomeSolid = FontFamily(Font(R.font.fa_solid_900))

/**
 * Draws one Font Awesome glyph at exactly [size], centred and tinted like a
 * vector icon so call sites do not care which set the glyph comes from.
 */
@Composable
fun ReclaimIcon(
    icon: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = 24.dp,
) {
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val style = remember(density, size) {
        TextStyle(
            fontFamily = FontAwesomeSolid,
            fontSize = with(density) { size.toSp() },
        )
    }
    val glyph = remember(icon) { icon.toChar().toString() }
    val layout = remember(measurer, glyph, style) { measurer.measure(glyph, style) }

    Canvas(
        modifier = modifier
            .size(size)
            .then(
                if (contentDescription == null) {
                    Modifier.clearAndSetSemantics {}
                } else {
                    Modifier.semantics {
                        this.contentDescription = contentDescription
                        role = Role.Image
                    }
                },
            ),
    ) {
        drawText(
            textLayoutResult = layout,
            color = tint,
            topLeft = Offset(
                x = (this.size.width - layout.size.width) / 2f,
                y = (this.size.height - layout.size.height) / 2f,
            ),
        )
    }
}
