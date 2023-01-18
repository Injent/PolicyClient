package components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import currentPlayer
import models.Building
import models.Field
import models.FieldState
import theme.Sound
import theme.Texture
import ui.Splash
import utils.BitmapImage
import utils.conditional
import utils.drawBitmap
import utils.playAudio

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun FieldCard(
    state: FieldState
) {
    Box(
        modifier = Modifier
            .size((96 * state.scale).dp)
            .onClick(
                matcher = PointerMatcher.mouse(PointerButton.Secondary),
                onClick = {
                    playAudio(Sound.CLICK)
                    state.onUnselect()
                }
            )
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val selectColor = if (state.selected) currentPlayer.color else Color.White

        val animatedColor by infiniteTransition.animateColor(
            initialValue = selectColor.copy(.85f),
            targetValue = selectColor.copy(.25f),
            animationSpec = infiniteRepeatable(
                animation = tween(500),
                repeatMode = RepeatMode.Reverse
            )
        )
        Splash(
            modifier = Modifier.align(Alignment.TopEnd),
            tooltip = {
                Text(text = state.field.name)
                Spacer(Modifier.height(8.dp))
                Row {
                    state.field.resources.forEach { resource ->
                        BitmapImage(
                            modifier = Modifier.size(32.dp),
                            resourcePath = "resource/${resource.type.name.lowercase()}.png"
                        )
                    }
                }
            }
        ) {
            var hover by remember { mutableStateOf(false) }

            Box(
                Modifier
                    .size((96 * state.scale).dp)
                    .onPointerEvent(PointerEventType.Enter) {
                        hover = true
                        playAudio(Sound.PRESSED)
                    }
                    .onPointerEvent(PointerEventType.Exit) { hover = false }
                    .onClick {
                        playAudio(Sound.SELECT)
                        state.onSelect(state.field)
                    }
                    .conditional(hover && !state.selected) {
                        border(width = 3.dp, color = animatedColor)
                    }
                    .conditional(state.selected) {
                        border(width = 4.dp, color = animatedColor)
                    }
            ) {
                Overlay(
                    overlay = {
                        Box(Modifier.size((96 * state.scale).dp)) {
                            FieldIcons(
                                state.field.buildings.map { it.type }
                            )
                            if (!state.field.isCaptured && state.field.lastCaptured != null) {
                                val offset = (16 * state.scale).dp
                                repeat(state.field.captureProgress) { time ->
                                    BitmapImage(
                                        modifier = Modifier
                                            .size((96 * state.scale).dp)
                                            .padding((30 * state.scale).dp)
                                            .align(Alignment.Center)
                                            .conditional(time == 0 && state.field.captureProgress == 2) {
                                                offset(x = -offset + 8.dp, y = -offset)
                                            }
                                            .conditional(time == 1 && state.field.captureProgress == 2) {
                                                offset(x = offset, y = offset - 4.dp)
                                            },
                                        resourcePath = "ui/flag.png",
                                        colorFilter = ColorFilter.lighting(state.fieldColor, Color.Transparent)
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size((96 * state.scale).dp)
                            .align(Alignment.Center)
                            .conditional(state.highlight && state.field.owner != null) {
                                fieldBorders(
                                    owner = state.field.owner,
                                    fieldColor = state.fieldColor,
                                    state = state
                                )
                            }
                            .conditional(state.field.type == Field.Type.FOREST || state.field.type == Field.Type.MOUNTAINS) {
                                fieldNature(if (state.field.type == Field.Type.FOREST) Texture.Trees else Texture.Rocks)
                            }
                            .fieldSides(state.scale, state.field.type, state)
                            .fieldPainter(state.field.type),
                    )
                }
            }
        }
    }
}

fun Modifier.fieldPainter(type: Field.Type) = composed {
    val bitmap = if (type == Field.Type.MOUNTAINS || type == Field.Type.FOREST) Texture.fields[Field.Type.PLAINS] else Texture.fields[type]

    drawWithContent {
        drawContent()

        bitmap?.let {
            drawImage(
                image = it,
                dstSize = IntSize(this.size.width.toInt(), this.size.height.toInt()),
                filterQuality = FilterQuality.None
            )
        }
    }
}

fun Modifier.fieldNature(texture: ImageBitmap) = composed {
    drawWithContent {
        drawContent()

        drawImage(
            image = texture,
            dstSize = IntSize(this.size.width.toInt(), this.size.height.toInt()),
            filterQuality = FilterQuality.None,
            srcOffset = IntOffset(0, 6)
        )
    }
}

@Composable
fun BoxScope.FieldIcons(
    buildings: List<Building.Type>,
) {
    val size = buildings.size

    LazyVerticalGrid(
        modifier = Modifier
            .width(if (size >= 2) 64.dp else 32.dp)
            .height(if (size == 1) 32.dp else 64.dp)
            .align(Alignment.Center),
        columns = GridCells.Fixed(if (size == 1) 1 else 2),
    ) {
        items(buildings) { building ->
            MiniIcon(
                modifier = Modifier.size(32.dp),
                texture = "buildings/${building.name.lowercase()}.png"
            )
        }
    }
}

fun Modifier.fieldSides(
    scale: Float = 1f,
    fieldType: Field.Type,
    state: FieldState
) = composed {
    drawWithContent {
        drawContent()

        state.states.forEachIndexed { index, sideState ->
            if (sideState == null) return@forEachIndexed
            val type = sideState.type
            val rotation = when (index) {
                0 -> -90f
                1 -> 0f
                2 -> 90f
                3 -> 180f
                else -> 0f
            }
            val bitmap: ImageBitmap
            if (type == Field.Type.WATER) {
                if (fieldType == Field.Type.DESERT)
                    bitmap = Texture.WaterSand
                else if (fieldType.isWithGrass)
                    bitmap = Texture.WaterGrass
                else if (fieldType == Field.Type.MESA)
                    bitmap = Texture.WaterRedSand
                else
                    return@forEachIndexed
            }
            else if (type == Field.Type.SWAMP) {
                if (fieldType != Field.Type.ISLAND && fieldType != Field.Type.SWAMP)
                    bitmap = Texture.SwampGrass
                else return@forEachIndexed
            }
            else if (type?.isWithGrass == true) {
                if (fieldType == Field.Type.DESERT || fieldType == Field.Type.MESA)
                    bitmap = Texture.Grass
                else return@forEachIndexed
            }
            else if (type == Field.Type.MESA) {
                if (fieldType == Field.Type.DESERT)
                    bitmap = Texture.RedSand
                else return@forEachIndexed
            }
            else if (type == Field.Type.ISLAND) {
                bitmap = if (fieldType == Field.Type.SWAMP)
                    Texture.WaterSwamp
                else if (fieldType == Field.Type.DESERT)
                    Texture.WaterSand
                else if (fieldType == Field.Type.MESA)
                    Texture.WaterRedSand
                else if (fieldType.isWithGrass)
                    Texture.WaterGrass
                else return@forEachIndexed
            } else return@forEachIndexed

            drawBitmap(
                bitmap = bitmap,
                rotation = rotation,
                size = IntSize(this.size.width.toInt(), (12 * scale).toInt())
            )
        }
    }
}

fun Modifier.fieldBorders(
    owner: String?,
    fieldColor: Color,
    state: FieldState
) = composed {
    val isTransparent = fieldColor == Color.Transparent

    drawWithContent {
        drawContent()

        clipRect {
            drawRect(
                color = if (isTransparent) fieldColor else fieldColor.copy(.25f),
                size = size
            )
            state.states.forEachIndexed { index, sideState ->
                val rotation = when (index) {
                    0 -> -90f
                    1 -> 0f
                    2 -> 90f
                    3 -> 180f
                    else -> 0f
                }
                if (sideState?.owner != owner) {
                    rotate(rotation) {
                        drawLine(
                            brush = SolidColor(if (isTransparent) fieldColor else fieldColor.copy(.6f)),
                            strokeWidth = 14.dp.toPx(),
                            cap = StrokeCap.Square,
                            start = Offset.Zero,
                            end = Offset(x = size.width, y = 0f)
                        )
                    }
                }
            }
        }
    }
}