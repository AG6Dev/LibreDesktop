package dev.ag6.libredesktop.repository.settings

import dev.ag6.libredesktop.model.reading.ReadingUnit
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setReadingUnits(units: ReadingUnit)
    fun getReadingUnits(): Flow<ReadingUnit>

    suspend fun setHighTarget(valueMgDl: Int)
    fun getHighTarget(): Flow<Int>

    suspend fun setLowTarget(valueMgDl: Int)
    fun getLowTarget(): Flow<Int>
}