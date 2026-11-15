package dev.reclaim.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import dev.reclaim.app.design.LocalSectionAccent
import dev.reclaim.app.design.SectionAccent
import dev.reclaim.app.ui.nav.ReclaimBackdrop

/**
 * Previews need the same two things the shell provides: the wash and the accent
 * the controls read.
 */
@Composable
fun SectionPreview(
    accent: SectionAccent,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalSectionAccent provides accent) {
        Box(modifier = Modifier.fillMaxSize()) {
            ReclaimBackdrop(accent = accent)
            content()
        }
    }
}
