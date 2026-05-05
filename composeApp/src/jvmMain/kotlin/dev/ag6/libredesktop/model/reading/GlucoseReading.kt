package dev.ag6.libredesktop.model.reading

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import dev.ag6.libredesktop.model.connection.GlucoseItem
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Serializable(with = TrendArrowSerializer::class)
enum class TrendArrow(val value: Int, val imageVector: ImageVector, val emoji: String) {
    RapidlyFalling(1, Icons.Default.South, "⬇️"), Falling(2, Icons.Default.SouthEast, "↘️"), Flat(
        3, Icons.Default.East, "➡️"
    ),
    Rising(4, Icons.Default.NorthEast, "↗️"), RapidlyRising(5, Icons.Default.North, "⬆️");

    companion object {
        fun trendArrowFromValue(value: Int): TrendArrow? = TrendArrow.entries.firstOrNull { it.value == value }
    }
}

object TrendArrowSerializer : KSerializer<TrendArrow> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("TrendArrow", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: TrendArrow) {
        encoder.encodeInt(value.value)
    }

    override fun deserialize(decoder: Decoder): TrendArrow {
        val value = decoder.decodeInt()
        return TrendArrow.trendArrowFromValue(value)
            ?: throw SerializationException("Unknown TrendArrow value: $value")
    }
}

@Serializable
data class GlucoseReading(
    val timestamp: Long,
    val valueInMgPerDl: Int,
    val trendArrow: TrendArrow?,
)

fun GlucoseItem.mapToGlucoseReading(): GlucoseReading {
    val dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a").withLocale(Locale.US)
    val localDateTime = LocalDateTime.parse(this.timestamp, dateFormatter)

    val convertedTimestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    return GlucoseReading(
        timestamp = convertedTimestamp,
        valueInMgPerDl = this.valueInMgPerDl,
        trendArrow = this.trendArrow?.let(TrendArrow::trendArrowFromValue),
    )
}

