package dev.ag6.libredesktop.ui.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
                            valueMgDl = state.lowTargetMgDl,
                            unit = state.readingUnit,
                            min = 40,
                            max = state.highTargetMgDl - step,
                            onValueChange = { onTargetsSaved(it, state.highTargetMgDl) }
                        )
                        TargetStepper(
                            label = "High Target:",
                            valueMgDl = state.highTargetMgDl,
                            unit = state.readingUnit,
                            min = state.lowTargetMgDl + step,
                            max = 400,
                            onValueChange = { onTargetsSaved(state.lowTargetMgDl, it) }
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
    valueMgDl: Int,
    unit: ReadingUnit,
    min: Int,
    max: Int,
    onValueChange: (Int) -> Unit
) {
    val step = if (unit == ReadingUnit.MMOL) 2 else 1
    var inputText by remember(unit) { mutableStateOf(unit.toDisplayValue(valueMgDl)) }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(valueMgDl, unit) {
        if (!isFocused) inputText = unit.toDisplayValue(valueMgDl)
    }

    fun commit() {
        val parsed = unit.parseDisplayValue(inputText)
        if (parsed != null && parsed in min..max) onValueChange(parsed)
        else inputText = unit.toDisplayValue(valueMgDl)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onValueChange(valueMgDl - step) },
                enabled = valueMgDl - step >= min,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease $label", modifier = Modifier.size(16.dp))
            }
            BasicTextField(
                value = inputText,
                onValueChange = {
                    inputText = it
                    val parsed = unit.parseDisplayValue(it)
                    if (parsed != null && parsed in min..max) onValueChange(parsed)
                },
                modifier = Modifier
                    .width(64.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.extraSmall)
                    .padding(horizontal = 6.dp, vertical = 5.dp)
                    .onFocusChanged { state ->
                        if (isFocused && !state.isFocused) commit()
                        isFocused = state.isFocused
                    },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { commit() })
            )
            IconButton(
                onClick = { onValueChange(valueMgDl + step) },
                enabled = valueMgDl + step <= max,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase $label", modifier = Modifier.size(16.dp))
            }
        }
    }
}
