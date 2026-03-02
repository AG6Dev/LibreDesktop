package dev.ag6.bs_app.ui.overview

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.ag6.bs_app.model.reading.GlucoseReading

class OverviewScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<OverviewScreenModel>()
        val state by screenModel.uiState.collectAsState()

        OverviewScreenContent(state.currentReading)
    }
}

@Composable
private fun OverviewScreenContent(currentReading: GlucoseReading? = null) {
    val readingMmol: Double = currentReading?.valueInMgPerDl?.div(18.0) ?: -1.0
    val readingStr = if (readingMmol >= 0) String.format("%.1f", readingMmol) else "N/A"
    Text(text = "Current Reading: $readingStr")
}