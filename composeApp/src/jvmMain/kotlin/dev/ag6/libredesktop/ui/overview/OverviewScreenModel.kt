package dev.ag6.libredesktop.ui.overview

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.ag6.libredesktop.AppContext
import dev.ag6.libredesktop.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OverviewScreenModel(
    appContext: AppContext,
    settingsRepository: SettingsRepository
) : ScreenModel {
    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            appContext.isLoaded.collect { loaded ->
                _uiState.update { it.copy(isLoading = !loaded) }
            }
        }

        screenModelScope.launch {
            appContext.currentReading.collect { reading ->
                _uiState.update { it.copy(currentReading = reading) }
            }
        }

        screenModelScope.launch {
            appContext.graphData.collect { data ->
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
