package dev.ag6.libredesktop.model.reading

import kotlin.math.roundToInt

enum class ReadingUnit(val label: String) {
    MMOL("mmol/L"),
    MGDL("mg/dL");

    fun toDisplayValue(valueInMgPerDl: Int): String {
        return when (this) {
            MMOL -> String.format("%.1f", valueInMgPerDl / 18.0)
            MGDL -> valueInMgPerDl.toString()
        }
    }

    fun parseDisplayValue(value: String): Int? {
        val normalized = value.trim()
        if (normalized.isEmpty()) return null

        return when (this) {
            MMOL -> normalized.toDoubleOrNull()?.let { (it * 18.0).roundToInt() }
            MGDL -> normalized.toIntOrNull()
        }
    }

    fun format(valueInMgPerDl: Int): String {
        return when (this) {
            MMOL -> String.format("%.1f %s", valueInMgPerDl / 18.0, label)
            MGDL -> "$valueInMgPerDl $label"
        }
    }

    fun formatValueOnly(valueInMgPerDl: Int): String {
        return toDisplayValue(valueInMgPerDl)
    }
}
