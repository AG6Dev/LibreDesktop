package dev.ag6.libredesktop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.ag6.libredesktop.ui.theme.statusHigh
import dev.ag6.libredesktop.ui.theme.statusInRange
import dev.ag6.libredesktop.ui.theme.statusLow

@Composable
fun SectionCard(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    actions: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cardPadding = if (compact) 14.dp else 22.dp
    val contentSpacing = if (compact) 10.dp else 18.dp
    val titleStyle = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = 4.dp,
        shadowElevation = if (compact) 4.dp else 10.dp
    ) {
        Column(
            modifier = Modifier.padding(cardPadding),
            verticalArrangement = Arrangement.spacedBy(contentSpacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(if (compact) 2.dp else 6.dp)) {
                    Text(title, style = titleStyle)
                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                actions?.invoke()
            }
            content()
        }
    }
}

@Composable
fun ScreenHeader(
    eyebrow: String? = null,
    title: String? = null,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (eyebrow != null) {
                Text(
                    text = eyebrow.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (title != null) {
                Text(text = title, style = MaterialTheme.typography.headlineLarge)
            }

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(16.dp))
            trailing()
        }
    }
}

@Composable
fun PreferenceRow(
    title: String,
    subtitle: String? = null,
    control: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        control()
    }
}

fun glucoseStatusColor(
    valueInMgPerDl: Int,
    lowTargetMgDl: Int,
    highTargetMgDl: Int
): Color {
    return when {
        valueInMgPerDl < lowTargetMgDl -> statusLow
        valueInMgPerDl > highTargetMgDl -> statusHigh
        else -> statusInRange
    }
}
