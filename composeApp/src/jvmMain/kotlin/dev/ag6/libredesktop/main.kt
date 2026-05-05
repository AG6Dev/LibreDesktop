package dev.ag6.libredesktop

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import com.kdroid.composetray.tray.api.ExperimentalTrayAppApi
import com.kdroid.composetray.tray.api.TrayApp
import com.kdroid.composetray.tray.api.rememberTrayAppState
import dev.ag6.libredesktop.di.initKoin
import dev.ag6.libredesktop.model.reading.ReadingUnit
import dev.ag6.libredesktop.model.reading.TrendArrow
import dev.ag6.libredesktop.repository.settings.SettingsRepository
import dev.ag6.libredesktop.ui.components.glucoseStatusColor
import dev.ag6.libredesktop.ui.dashboard.TrendArrowBadge
import dev.ag6.libredesktop.ui.theme.statusInRange
import org.koin.compose.koinInject

@OptIn(ExperimentalTrayAppApi::class)
fun main() {
    initKoin()
    application {
        val appState = koinInject<GlobalAppState>()
        val settingsRepository = koinInject<SettingsRepository>()
        val currentReading by appState.currentReading.collectAsState()
        val readingUnit by settingsRepository.getReadingUnits().collectAsState(initial = ReadingUnit.MMOL)
        val lowTargetMgDl by settingsRepository.getLowTarget().collectAsState(initial = 70)
        val highTargetMgDl by settingsRepository.getHighTarget().collectAsState(initial = 180)

        val trayIconColor =
            currentReading?.let { glucoseStatusColor(it.valueInMgPerDl, lowTargetMgDl, highTargetMgDl) }
                ?: statusInRange
        val trayTooltip = currentReading?.let { reading ->
            val trendLabel = reading.trendArrow?.emoji?.let { " $it" }.orEmpty()
            "LibreDesktop ${readingUnit.format(reading.valueInMgPerDl)}$trendLabel"
        } ?: "LibreDesktop"

        val trayAppState = rememberTrayAppState(
            initialWindowSize = DpSize(400.dp, 520.dp),
            initiallyVisible = true,
        )

        TrayApp(
            state = trayAppState,
            iconContent = {
                if (currentReading != null)
                    TrendArrowBadge(
                        currentReading?.trendArrow ?: TrendArrow.Flat,
                        trayIconColor,
                        Modifier.fillMaxSize()
                    )
                else
                //TODO: replace with a proper logo
                    Icon(
                        imageVector = Icons.Default.QuestionMark,
                        contentDescription = "No reading available",
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
            },
            tooltip = trayTooltip,

            transparent = true,
            undecorated = true,
            resizable = false,
            windowsTitle = "LibreDesktop",
            //TODO: have a proper logo here
            windowIcon = null,

            exitTransition = ExitTransition.None,
            enterTransition = EnterTransition.None,

            menu = {
                Item("Open") { trayAppState.toggle() }
                Item("Quit") { exitApplication() }
            }
        ) {
            TrayWindowContent(cornerRadius = 12.dp) {
                App()
            }
        }
    }
}

@Composable
private fun TrayWindowContent(
    cornerRadius: Dp,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clip(RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
        tonalElevation = 6.dp,
        shadowElevation = 12.dp,
    ) {
        Box(Modifier.fillMaxSize()) {
            content()
        }
    }
}
