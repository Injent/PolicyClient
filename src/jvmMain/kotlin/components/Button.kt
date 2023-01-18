package components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import theme.Texture
import utils.BitmapImage
import utils.bounceClick

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BitmapSquareButton(
    modifier: Modifier = Modifier.size(48.dp),
    texture: String,
    lockedTexture: String,
    onClick: () -> Unit,
    locked: Boolean = false,
) {
    BitmapImage(
        modifier = modifier
            .onClick {
                onClick()
            },
        resourcePath = if (!locked) texture else lockedTexture
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LargeBitmapButton(
    modifier: Modifier = Modifier,
    texture: String,
    lockedTexture: String,
    onClick: () -> Unit,
    locked: Boolean = false,
    text: String
) {
    Box(
        modifier
    ) {
        BitmapImage(
            modifier = modifier
                .height(48.dp)
                .width(192.dp)
                .align(Alignment.Center)
                .bounceClick()
                .onClick {
                    onClick()
                },
            resourcePath = if (locked) lockedTexture else texture
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text
        )
    }
}

fun Modifier.menu() = composed {
    drawBehind {
        drawImage(
            image = Texture.MenuTop,
            filterQuality = FilterQuality.None,
            dstSize = IntSize(320, 24)
        )
        drawImage(
            image = Texture.MenuSized,
            filterQuality = FilterQuality.None,
            dstSize = IntSize(320, size.height.toInt() - 48),
            dstOffset = IntOffset(0, 24)
        )
        drawImage(
            image = Texture.MenuBottom,
            filterQuality = FilterQuality.None,
            dstSize = IntSize(320, 24),
            dstOffset = IntOffset(0, size.height.toInt() - 24)
        )
    }
}