package dev.ag6.bs_app.ui.overview

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.ag6.bs_app.repository.readings.ReadingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OverviewScreenModel(repository: ReadingsRepository) : ScreenModel {
    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isLoading = true) }

        //todo: make it scheduled to update every minute
        screenModelScope.launch {
            val currentReading = repository.getCurrentReading()
            _uiState.update { it.copy(isLoading = false, currentReading = currentReading) }
        }
    }
}