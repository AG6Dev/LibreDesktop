package dev.ag6.libredesktop.notifications

import dev.ag6.libredesktop.GlobalAppState
import dev.ag6.libredesktop.model.alarms.AlarmSettings
import dev.ag6.libredesktop.model.reading.GlucoseReading
import dev.ag6.libredesktop.model.reading.ReadingUnit
import dev.ag6.libredesktop.repository.settings.SettingsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import java.awt.Toolkit
import java.io.File
import javax.sound.sampled.AudioSystem
import kotlin.time.Duration.Companion.minutes

data class AlertEvent(val title: String, val message: String, val isHigh: Boolean)

class GlucoseAlertNotifier(
    private val globalAppState: GlobalAppState,
    private val settingsRepository: SettingsRepository,
) : AutoCloseable {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _alerts = MutableSharedFlow<AlertEvent>(extraBufferCapacity = 4)
    val alerts: SharedFlow<AlertEvent> = _alerts.asSharedFlow()
    private var lastAlertTime: Long = 0L

    init {
        scope.launch {
            combine(
                globalAppState.currentReading,
                settingsRepository.getAlarmSettings(),
                settingsRepository.getLowTarget(),
                settingsRepository.getHighTarget(),
                settingsRepository.getReadingUnits(),
            ) { reading, alarmSettings, lowTarget, highTarget, readingUnit ->
                AlarmCheckData(reading, alarmSettings, lowTarget, highTarget, readingUnit)
            }.collect { checkAndFire(it) }
        }

    }

    private fun checkAndFire(data: AlarmCheckData) {

        val (reading, settings, lowTarget, highTarget) = data
        if (!settings.alarmsEnabled || reading == null) return

        val outOfRange = reading.valueInMgPerDl !in lowTarget..highTarget
        if (!outOfRange) return

        val now = System.currentTimeMillis()
        if (now - lastAlertTime < settings.alarmInterval.minutes.inWholeMilliseconds) return
        lastAlertTime = now

        val formattedValue = data.readingUnit.format(reading.valueInMgPerDl)
        val isHigh = reading.valueInMgPerDl > highTarget
        val level = if (isHigh) "HIGH" else "LOW"
        val trend = data.reading?.trendArrow?.emoji ?: ""
        val message = "Glucose $level: $formattedValue\nTrending: $trend"

        if (settings.notificationsEnabled) {
            _alerts.tryEmit(AlertEvent("Glucose Alert", message, isHigh))
        }

        if (settings.soundEnabled) {
            scope.launch(Dispatchers.IO) { playSound(settings.customSoundPath) }
        }
    }

    private suspend fun playSound(path: String?) = withContext(Dispatchers.IO) {
        if (path == null) {
            Toolkit.getDefaultToolkit().beep()
            return@withContext
        }
        try {
            val stream = AudioSystem.getAudioInputStream(File(path))
            val clip = AudioSystem.getClip()
            clip.open(stream)
            clip.start()
            delay(clip.microsecondLength / 1000 + 200)
            clip.close()
        } catch (_: Exception) {
        }
    }

    override fun close() {
        scope.cancel()
    }

    private data class AlarmCheckData(
        val reading: GlucoseReading?,
        val alarmSettings: AlarmSettings,
        val lowTarget: Int,
        val highTarget: Int,
        val readingUnit: ReadingUnit,
    )
}
