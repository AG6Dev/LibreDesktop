package dev.ag6.libredesktop.ui.dashboard

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.ag6.libredesktop.GlobalAppState
import dev.ag6.libredesktop.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardScreenModel(
    globalAppState: GlobalAppState,
    settingsRepository: SettingsRepository
) : ScreenModel {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            globalAppState.isLoaded.collect { loaded ->
                _uiState.update { it.copy(isLoading = !loaded) }
            }
        }

        screenModelScope.launch {
            globalAppState.currentReading.collect { reading ->
                _uiState.update { it.copy(currentReading = reading) }
            }
        }

        screenModelScope.launch {
            globalAppState.graphData.collect { data ->
                _uiState.update { it.copy(graphData = data) }
            }
        }

        screenModelScope.launch {
            settingsRepository.getReadingUnits().collect { readingUnit ->
                _uiState.update { it.copy(readingUnit = readingUnit) }
            }
        }

        screenModelScope.launch {
            settingsRepository.getHighTarget().collect { highTargetMgDl ->
                _uiState.update { it.copy(highTargetMgDl = highTargetMgDl) }
            }
        }

        screenModelScope.launch {
            settingsRepository.getLowTarget().collect { lowTargetMgDl ->
                _uiState.update { it.copy(lowTargetMgDl = lowTargetMgDl) }
            }
        }
    }
}
