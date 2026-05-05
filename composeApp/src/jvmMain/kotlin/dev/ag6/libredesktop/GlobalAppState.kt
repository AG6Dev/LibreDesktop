package dev.ag6.libredesktop

import dev.ag6.libredesktop.model.reading.GlucoseReading
import dev.ag6.libredesktop.model.theme.ThemeMode
import dev.ag6.libredesktop.repository.readings.ReadingsRepository
import dev.ag6.libredesktop.repository.settings.SettingsRepository
import dev.ag6.libredesktop.util.scheduleRepeatingTask
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.minutes

class GlobalAppState(
    settingsRepository: SettingsRepository,
    private val readingsRepository: ReadingsRepository
) : AutoCloseable {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _currentReading = MutableStateFlow<GlucoseReading?>(null)
    val currentReading: StateFlow<GlucoseReading?> = _currentReading.asStateFlow()

    private val _lowTargetMgDl = MutableStateFlow(70)
    val lowTargetMgDl: StateFlow<Int> = _lowTargetMgDl.asStateFlow()

    private val _highTargetMgDl = MutableStateFlow(180)
    val highTargetMgDl: StateFlow<Int> = _highTargetMgDl.asStateFlow()

    private val _graphData = MutableStateFlow<List<GlucoseReading>>(emptyList())
    val graphData: StateFlow<List<GlucoseReading>> = _graphData.asStateFlow()

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    init {
        scope.launch {
            settingsRepository.getThemeMode().collect { mode ->
                _themeMode.update { mode }
            }
        }

        scope.launch {
            settingsRepository.getLowTarget().collect { lowTarget ->
                _lowTargetMgDl.update { lowTarget }
            }
        }

        scope.launch {
            settingsRepository.getHighTarget().collect { highTarget ->
                _highTargetMgDl.update { highTarget }
            }
        }

        scope.scheduleRepeatingTask(1.minutes) {
            _currentReading.update { readingsRepository.getCurrentReading() }
            _graphData.update { readingsRepository.getGraphReadings() }
            _isLoaded.update { true }
        }
    }

    override fun close() {
        scope.cancel()
    }
}
