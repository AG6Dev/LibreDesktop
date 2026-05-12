package dev.ag6.libredesktop.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import com.kdroid.composetray.tray.api.ExperimentalTrayAppApi
import com.kdroid.composetray.tray.api.TrayAppState
import dev.ag6.libredesktop.model.theme.ThemeMode
import dev.ag6.libredesktop.ui.theme.LibreDesktopTheme
import dev.ag6.libredesktop.ui.theme.statusHigh
import dev.ag6.libredesktop.ui.theme.statusLow
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

private data class VisibleAlert(
    val id: Long,
    val alert: AlertEvent,
)

@OptIn(ExperimentalTrayAppApi::class)
@Composable
fun NotificationHost(
    notifier: GlucoseAlertNotifier,
    trayState: TrayAppState,
    themeMode: ThemeMode,
    modifier: Modifier = Modifier,
) {
    val isVisible by trayState.isVisible.collectAsState()
    if (!isVisible) {

        var visibleAlert by remember { mutableStateOf<VisibleAlert?>(null) }
        var nextAlertId by remember { mutableStateOf(0L) }

        LaunchedEffect(notifier) {
            notifier.alerts.collect { alert ->
                visibleAlert = VisibleAlert(nextAlertId++, alert)
            }
        }

        LaunchedEffect(visibleAlert?.id) {
            val alertId = visibleAlert?.id ?: return@LaunchedEffect
            delay((visibleAlert?.alert?.displaySeconds ?: 3).seconds)
            if (visibleAlert?.id == alertId) {
                visibleAlert = null
            }
        }

        val currentAlert = visibleAlert ?: return
        val state = rememberDialogState(
            width = 380.dp,
            height = 96.dp,
            position = WindowPosition(Alignment.BottomEnd),
        )

        DialogWindow(
            onCloseRequest = { visibleAlert = null },
            state = state,
            title = currentAlert.alert.title,
            undecorated = true,
            transparent = true,
            focusable = false,
            resizable = false,
            alwaysOnTop = true
        ) {
            LibreDesktopTheme(themeMode) {
                CustomNotification(
                    alert = currentAlert.alert,
                    onDismiss = { visibleAlert = null },
                    modifier = modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun CustomNotification(
    alert: AlertEvent,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = if (alert.isHigh) statusHigh else statusLow

    Box(
        modifier = modifier.padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 8.dp,
            shadowElevation = 16.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = accent,
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                    )
                    Text(
                        text = alert.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .pointerHoverIcon(PointerIcon.Hand),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss notification",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
