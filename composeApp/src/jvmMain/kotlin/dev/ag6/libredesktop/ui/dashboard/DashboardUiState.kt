package dev.ag6.libredesktop.ui.dashboard

import dev.ag6.libredesktop.model.reading.GlucoseReading
import dev.ag6.libredesktop.model.reading.ReadingUnit

data class DashboardUiState(
    val isLoading: Boolean = false,
    val graphData: List<GlucoseReading> = listOf(),
    val currentReading: GlucoseReading? = null,
    val readingUnit: ReadingUnit = ReadingUnit.MMOL,
    val highTargetMgDl: Int = 180,
    val lowTargetMgDl: Int = 70
)
