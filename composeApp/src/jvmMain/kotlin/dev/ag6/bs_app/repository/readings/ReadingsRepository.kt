package dev.ag6.bs_app.repository.readings

import dev.ag6.bs_app.model.reading.GlucoseReading

interface ReadingsRepository {
    suspend fun getLastThirtyReadings(): List<GlucoseReading>

    suspend fun getCurrentReading(): GlucoseReading?
}