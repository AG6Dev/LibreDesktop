package dev.ag6.libredesktop.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import dev.ag6.libredesktop.model.reading.ReadingUnit
import dev.ag6.libredesktop.ui.auth.AuthScreen

class SettingsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SettingsScreenModel>()
        val state by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.current

        SettingsScreenContent(
            state = state,
            onBack = { navigator?.pop() },
            onReadingUnitSelected = screenModel::onReadingUnitSelected,
            onTargetsSaved = screenModel::onTargetsSaved,
            onLogout = {
                screenModel.onLogout {
                    navigator?.replaceAll(AuthScreen())
                }
            }
        )
    }
}

@Composable
private fun SettingsScreenContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onReadingUnitSelected: (ReadingUnit) -> Unit,
    onTargetsSaved: (Int, Int) -> Unit,
    onLogout: () -> Unit
) {
    var lowTargetInput by remember(state.readingUnit) { mutableStateOf("") }
    var highTargetInput by remember(state.readingUnit) { mutableStateOf("") }
    var targetError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.lowTargetMgDl, state.highTargetMgDl, state.readingUnit) {
        lowTargetInput = state.readingUnit.toDisplayValue(state.lowTargetMgDl)
        highTargetInput = state.readingUnit.toDisplayValue(state.highTargetMgDl)
        targetError = null
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onBack) {
                    Text("Back")
                }
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(80.dp))
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.TopStart
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AccountSection(
                        email = state.email,
                        onLogout = onLogout
                    )
                    HorizontalDivider()
                    Text(
                        text = "Glucose units",
                        style = MaterialTheme.typography.titleMedium
                    )
                    ReadingUnit.entries.forEach { unit ->
                        ReadingUnitRow(
                            unit = unit,
                            selected = state.readingUnit == unit,
                            onSelected = { onReadingUnitSelected(unit) }
                        )
                    }
                    HorizontalDivider()
                    TargetRangeSection(
                        readingUnit = state.readingUnit,
                        lowTargetInput = lowTargetInput,
                        highTargetInput = highTargetInput,
                        errorMessage = targetError,
                        onLowTargetChanged = {
                            lowTargetInput = it
                            targetError = null
                        },
                        onHighTargetChanged = {
                            highTargetInput = it
                            targetError = null
                        },
                        onSave = {
                            val lowTargetMgDl = state.readingUnit.parseDisplayValue(lowTargetInput)
                            val highTargetMgDl = state.readingUnit.parseDisplayValue(highTargetInput)
                            if (lowTargetMgDl == null || highTargetMgDl == null) {
                                targetError = "Enter valid low and high target values."
                            } else {
                                val validationError = when {
                                    lowTargetMgDl < 40 || highTargetMgDl > 400 -> {
                                        "Targets must stay within 40 to 400 mg/dL."
                                    }

                                    lowTargetMgDl >= highTargetMgDl -> {
                                        "Low target must be below high target."
                                    }

                                    else -> null
                                }

                                targetError = validationError

                                if (validationError == null) {
                                    onTargetsSaved(lowTargetMgDl, highTargetMgDl)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetRangeSection(
    readingUnit: ReadingUnit,
    lowTargetInput: String,
    highTargetInput: String,
    errorMessage: String?,
    onLowTargetChanged: (String) -> Unit,
    onHighTargetChanged: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Target range",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = lowTargetInput,
            onValueChange = onLowTargetChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Low target") },
            suffix = { Text(readingUnit.label) }
        )
        OutlinedTextField(
            value = highTargetInput,
            onValueChange = onHighTargetChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("High target") },
            suffix = { Text(readingUnit.label) }
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save targets")
        }
    }
}

@Composable
private fun AccountSection(
    email: String?,
    onLogout: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Account",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = email ?: "Email unavailable",
            style = MaterialTheme.typography.bodyLarge
        )
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}

@Composable
private fun ReadingUnitRow(
    unit: ReadingUnit,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelected)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelected
        )
        Text(
            text = unit.label,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
