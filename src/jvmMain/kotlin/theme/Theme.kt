package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorPalette = darkColors(
    background = DarkBackground,
    primary = Color.White,
    primaryVariant = PrimaryVariant,
    onPrimary = DarkBackground,
    secondary = Secondary,
    secondaryVariant = SecondaryVariant,
    onSecondary = Color.White,

    )

@Composable
fun PolicyTheme(
    //darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = DarkColorPalette

    MaterialTheme(
        colors = colors,
        shapes = Shapes,
        typography = Typography,
        content = content
    )
}