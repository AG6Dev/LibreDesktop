package dev.ag6.libredesktop.ui.settings

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.ag6.libredesktop.model.reading.ReadingUnit
import dev.ag6.libredesktop.model.theme.ThemeMode
import dev.ag6.libredesktop.ui.auth.AuthScreen
import dev.ag6.libredesktop.ui.components.PreferenceRow
import dev.ag6.libredesktop.ui.components.SectionCard
import dev.ag6.libredesktop.ui.components.ValueStepper
import kotlin.math.roundToInt

object SettingsScreen : Tab {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SettingsScreenModel>()
        val state by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.current

        SettingsScreenContent(
            state = state,
            onThemeModeSelected = screenModel::onThemeModeSelected,
            onReadingUnitSelected = screenModel::onReadingUnitSelected,
            onTargetsSaved = screenModel::onTargetsSaved,
            onLaunchOnStartupChanged = screenModel::onLaunchOnStartupChanged,
            onLogout = {
                screenModel.onLogout {
                    navigator?.parent?.replaceAll(AuthScreen())
                }
            })
    }

    override val options: TabOptions
        @Composable get() {
            val icon = rememberVectorPainter(Icons.Default.Settings)
            return remember {
                TabOptions(3u, "Settings", icon)
            }
        }
}

@Composable
private fun SettingsScreenContent(
    state: SettingsUiState,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onReadingUnitSelected: (ReadingUnit) -> Unit,
    onTargetsSaved: (Int, Int) -> Unit,
    onLaunchOnStartupChanged: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                SectionCard(title = "Appearance", compact = true) {
                    PreferenceRow(title = "Theme:") {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ThemeMode.entries.forEach { mode ->
                                FilterChip(
                                    selected = state.themeMode == mode,
                                    onClick = { onThemeModeSelected(mode) },
                                    label = { Text(mode.label, style = MaterialTheme.typography.labelSmall) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                                    )
                                )
                            }
                        }
                    }
                    PreferenceRow(title = "Glucose Units:") {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ReadingUnit.entries.forEach { unit ->
                                FilterChip(
                                    selected = state.readingUnit == unit,
                                    onClick = { onReadingUnitSelected(unit) },
                                    label = { Text(unit.label, style = MaterialTheme.typography.labelSmall) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                SectionCard(title = "Target range", compact = true) {
                    val step = if (state.readingUnit == ReadingUnit.MMOL) 2 else 1
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        TargetStepper(
                            label = "Low Target:",
                            value = state.lowTargetMgDl,
                            unit = state.readingUnit,
                            min = 40,
                            max = state.highTargetMgDl - step,
                            onValueChange = { onTargetsSaved(it, state.highTargetMgDl) }
                        )
                        TargetStepper(
                            label = "High Target:",
                            value = state.highTargetMgDl,
                            unit = state.readingUnit,
                            min = state.lowTargetMgDl + step,
                            max = 400,
                            onValueChange = { onTargetsSaved(state.lowTargetMgDl, it) }
                        )
                    }
                }
            }

            item {
                SectionCard(title = "Startup", compact = true) {
                    PreferenceRow(
                        title = "Launch on startup",
                        subtitle = if (state.isAutoStartSupported) {
                            "Start LibreDesktop in the tray when you sign in."
                        } else {
                            "Available from a distributed app build."
                        }
                    ) {
                        Switch(
                            checked = state.launchOnStartup,
                            onCheckedChange = onLaunchOnStartupChanged,
                            enabled = state.isAutoStartSupported && !state.isUpdatingAutoStart
                        )
                    }
                }
            }

            item {
                SectionCard(title = "Account", compact = true) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val emailInteraction = remember { MutableInteractionSource() }
                        val emailHovered by emailInteraction.collectIsHoveredAsState()
                        val email = state.email ?: "Email unavailable"
                        Row(modifier = Modifier.hoverable(emailInteraction).weight(1f)) {
                            Text(
                                text = email.take(3),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = email.drop(3),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.blur(if (emailHovered) 0.dp else 12.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(onClick = onLogout) {
                            Text("Log out")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TargetStepper(
    label: String,
    value: Int,
    unit: ReadingUnit,
    min: Int,
    max: Int,
    onValueChange: (Int) -> Unit
) {
    fun stepValue(valueMgDl: Float, deltaMmol: Double): Float {
        val currentMmol = unit.toDisplayValue(valueMgDl.roundToInt()).toDoubleOrNull() ?: return valueMgDl
        return ((currentMmol + deltaMmol) * 18.0).roundToInt().toFloat()
    }

    ValueStepper(
        label = label,
        value = value.toFloat(),
        step = 1f,
        min = min.toFloat(),
        max = max.toFloat(),
        allowDecimal = unit == ReadingUnit.MMOL,
        valueFormatter = { unit.toDisplayValue(it.roundToInt()) },
        valueParser = { unit.parseDisplayValue(it)?.toFloat() },
        previousValue = { if (unit == ReadingUnit.MMOL) stepValue(it, -0.1) else it - 1f },
        nextValue = { if (unit == ReadingUnit.MMOL) stepValue(it, 0.1) else it + 1f },
        onValueChange = { onValueChange(it.roundToInt()) }
    )
}
