package dev.ag6.libredesktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.navigator.Navigator
import dev.ag6.libredesktop.ui.auth.AuthScreen
import dev.ag6.libredesktop.ui.theme.LibreDesktopTheme
import org.koin.compose.koinInject

//TODO: When the user has multiple patients attached, allow them to change between the patients
//TODO: Add run on startup
@Composable
fun App() {
    val globalAppState = koinInject<GlobalAppState>()
    val themeMode by globalAppState.themeMode.collectAsState()

    LibreDesktopTheme(themeMode = themeMode) {
        Navigator(screen = AuthScreen())
    }
}
