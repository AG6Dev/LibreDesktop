package dev.ag6.libredesktop.model.alarms

import kotlinx.serialization.Serializable

@Serializable
data class AlarmSettings(
    val alarmsEnabled: Boolean = false,
    val alarmInterval: Int = 5,
    val soundEnabled: Boolean = false,
    val customSoundPath: String? = null,
    val notificationsEnabled: Boolean = false,
    val notificationVisibilityLength: Int = 3,
    val notificationTitleTemplate: String = DEFAULT_NOTIFICATION_TITLE_TEMPLATE,
    val notificationMessageTemplate: String = DEFAULT_NOTIFICATION_MESSAGE_TEMPLATE,
) {
    companion object {
        val ALARM_INTERVALS = listOf(1, 5, 15, 30)

        const val DEFAULT_NOTIFICATION_TITLE_TEMPLATE = "Glucose {level}"
        const val DEFAULT_NOTIFICATION_MESSAGE_TEMPLATE = "{value} {trend}"
    }
}
