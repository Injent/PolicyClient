
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.window.*
import data.Settings
import di.initKoin
import models.Player
import org.koin.core.Koin
import theme.PolicyTheme
import ui.PlayScreen
import ui.menu.JoinScreen
import java.io.File


lateinit var koin: Koin
lateinit var ipGlobal: String
lateinit var currentPlayer: Player
lateinit var settings: Settings

@Composable
@Preview
fun FrameWindowScope.App() {
    PolicyTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            var started by remember { mutableStateOf(false) }

            if (!started) {
                JoinScreen(
                    onStart = { started = true }
                )
            }
            if (started) {
                PlayScreen()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    koin = initKoin().koin
    settings = Settings(File("settings.txt"))

    application {
        val state = rememberWindowState(placement = WindowPlacement.Floating)

        Window(
            onCloseRequest = ::exitApplication,
            title = "Country Contest",
            state = state,
            onKeyEvent = {
                if (it.key == Key.F11) {
                    state.placement = if (state.placement == WindowPlacement.Floating) WindowPlacement.Fullscreen else WindowPlacement.Floating
                    true
                } else false
            }
        ) {
            App()
        }
    }
}