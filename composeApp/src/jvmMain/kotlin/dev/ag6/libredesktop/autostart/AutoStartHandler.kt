package dev.ag6.libredesktop.autostart

import io.github.vinceglb.autolaunch.AutoLaunch
import kotlinx.coroutines.runBlocking

class AutoStartHandler {
    private val autoLaunch = AutoLaunch(appPackageName = APP_PACKAGE_NAME)

    fun isSupported(): Boolean {
        return AutoLaunch.isRunningFromDistributable
    }

    fun isStartedViaAutoStart(): Boolean {
        return autoLaunch.isStartedViaAutostart()
    }

    fun isEnabled(): Boolean = runBlocking {
        autoLaunch.isEnabled()
    }

    fun setEnabled(enabled: Boolean) = runBlocking {
        if (enabled) {
            autoLaunch.enable()
        } else {
            autoLaunch.disable()
        }
    }

    private companion object {
        const val APP_PACKAGE_NAME = "dev.ag6.libredesktop"
    }
}
