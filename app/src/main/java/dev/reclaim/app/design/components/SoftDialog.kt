package dev.reclaim.app.design.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.Space

/**
 * A confirmation in the app's own language: white card, two pills, no material
 * chrome.
 */
@Composable
fun SoftDialog(
    title: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        SoftCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = ReclaimColors.Ink,
            )
            Spacer(Modifier.height(Space.sm))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = ReclaimColors.InkSecondary,
            )
            Spacer(Modifier.height(Space.xl))
            PillButton(
                text = confirmLabel,
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Space.sm))
            PillButton(
                text = dismissLabel,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                style = PillStyle.Secondary,
            )
        }
    }
}
