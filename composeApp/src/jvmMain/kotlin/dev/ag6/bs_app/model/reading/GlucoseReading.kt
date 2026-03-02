package dev.ag6.bs_app.model.reading

import dev.ag6.bs_app.model.connection.GlucoseItem
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class GlucoseReading(
    val timestamp: Long,
    val valueInMgPerDl: Int,
    val trendArrow: Int
)

fun GlucoseItem.mapToGlucoseReading(): GlucoseReading {
    val dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a").withLocale(Locale.US)
    val localDateTime = LocalDateTime.parse(this.timestamp, dateFormatter)

    val convertedTimestamp = localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()

    return GlucoseReading(
        timestamp = convertedTimestamp,
        valueInMgPerDl = this.valueInMgPerDl,
        trendArrow = this.trendArrow
    )
}



