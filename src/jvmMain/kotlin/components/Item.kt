package components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import currentPlayer
import models.Building
import models.Field
import theme.Sound
import utils.BitmapImage
import utils.conditional
import utils.playAudio

@Composable
fun ResourceItem(
    modifier: Modifier = Modifier.size(56.dp),
    count: Int = 0,
    resourcePath: String
) {
    Box(
        Modifier.wrapContentSize()
    ) {
        BitmapImage(
            modifier = modifier,
            resourcePath = resourcePath
        )
        Text(
            modifier = Modifier.align(Alignment.BottomEnd),
            text = count.toString()
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun MiniField(
    modifier: Modifier = Modifier,
    onSelect: () -> Unit,
    field: () -> Field,
) {
    var hover by remember { mutableStateOf(false) }

    Box(
        Modifier
            .size(80.dp)
            .onPointerEvent(PointerEventType.Enter) { hover = true }
            .onPointerEvent(PointerEventType.Exit) { hover = false }
            .onClick {
                playAudio(Sound.SELECT)
                onSelect()
            }
            .conditional(hover) {
                border(width = 3.dp, color = Color.White)
            }
    ) {
        BitmapImage(
            modifier = modifier.size(80.dp),
            resourcePath = "${field().type.name.lowercase()}.png"
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(if (field().owner == currentPlayer.name) currentPlayer.color.copy(.5f) else Color.Transparent)
        ) {
            if (field().buildings.any { it.type == Building.Type.MILITARY_BASE })
                BitmapImage(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    resourcePath = "buildings/mini_military_base.png"
                )
        }
    }
}