package dev.ag6.libredesktop.model.reading

import dev.ag6.libredesktop.model.connection.GlucoseItem
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Serializable
data class GlucoseReading(
    val timestamp: Long,
    val valueInMgPerDl: Int,
    val trendArrow: TrendArrow?,
)

fun GlucoseItem.asGlucoseReading(): GlucoseReading {
    val dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a").withLocale(Locale.US)
    val localDateTime = LocalDateTime.parse(this.timestamp, dateFormatter)

    val convertedTimestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    return GlucoseReading(
        timestamp = convertedTimestamp,
        valueInMgPerDl = this.valueInMgPerDl,
        trendArrow = this.trendArrow?.let(TrendArrow::fromValue),
    )
}

