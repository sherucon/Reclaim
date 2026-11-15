package dev.reclaim.app.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object ReclaimShapes {
    val Card = RoundedCornerShape(28.dp)
    val Tile = RoundedCornerShape(20.dp)
    val Input = RoundedCornerShape(18.dp)
    val Sheet = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
}

val ReclaimMaterialShapes =
    Shapes(
        extraSmall = RoundedCornerShape(12.dp),
        small = ReclaimShapes.Input,
        medium = ReclaimShapes.Tile,
        large = ReclaimShapes.Card,
        extraLarge = RoundedCornerShape(32.dp),
    )
