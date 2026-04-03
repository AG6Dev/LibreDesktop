package dev.ag6.libredesktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab


@Composable
fun SidebarNavigation(
    tabs: List<Tab>,
    currentTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp),
        color = colors.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colors.primary.copy(alpha = 0.10f),
                            colors.surface,
                            colors.secondary.copy(alpha = 0.06f)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "LibreDesktop",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = colors.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            tabs.forEach { tab ->
                val isSelected = tab == currentTab
                NavigationDrawerItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        tab.options.icon?.let { painter ->
                            Icon(
                                painter = painter,
                                contentDescription = tab.options.title,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    label = { Text(tab.options.title) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = colors.primary.copy(alpha = 0.14f),
                        selectedTextColor = colors.onSurface,
                        selectedIconColor = colors.primary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
