package components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import utils.BitmapImage

@Composable
fun MiniIcon(
    modifier: Modifier = Modifier.size(16.dp),
    texture: String,
) {
    BitmapImage(
        modifier,
        resourcePath = texture
    )
}

@Composable
fun Overlay(
    modifier: Modifier = Modifier,
    overlay: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .onGloballyPositioned { }
    ) {
        content()
        Box(
            Modifier
                .onGloballyPositioned { }
        ) {
            overlay()
        }
    }
}


