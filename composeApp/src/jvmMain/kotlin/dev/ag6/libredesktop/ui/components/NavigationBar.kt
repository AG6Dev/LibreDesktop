package dev.ag6.libredesktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab

@Composable
fun NavigationBar(
    tabs: List<Tab>,
    currentTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.height(64.dp).fillMaxWidth(),
        color = colors.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentTab.options.title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.primary
            )

            Spacer(Modifier.weight(1f))

            tabs.forEach { tab ->
                val isSelected = tab == currentTab
                NavItem(
                    tab = tab,
                    isSelected = isSelected,
                    onClick = { onTabSelected(tab) }
                )
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}

@Composable
private fun NavItem(
    tab: Tab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val containerColor = if (isSelected) colors.primary.copy(alpha = 0.14f) else colors.surface.copy(alpha = 0f)
    val contentColor = if (isSelected) colors.primary else colors.onSurfaceVariant

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        tab.options.icon?.let { painter ->
            Icon(
                painter = painter,
                contentDescription = tab.options.title,
                modifier = Modifier.size(22.dp),
                tint = contentColor
            )
        }
    }
}
