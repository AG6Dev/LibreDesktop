package dev.ag6.libredesktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import dev.ag6.libredesktop.ui.auth.AuthScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        Navigator(screen = AuthScreen())
    }
}
