package dev.ag6.libredesktop.ui.overview

import dev.ag6.libredesktop.model.reading.GlucoseReading

data class OverviewUiState(
    val isLoading: Boolean = false,
    val graphData: List<GlucoseReading> = listOf(),
    val currentReading: GlucoseReading? = null
)