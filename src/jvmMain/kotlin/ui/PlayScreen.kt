package ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.*
import currentPlayer
import koin
import kotlinx.coroutines.delay
import models.Building
import models.Field
import models.Player
import models.Resource
import theme.Sound
import theme.Texture
import theme.Water
import utils.BitmapImage
import utils.bounceClick
import utils.playAudio

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun PlayScreen(
    viewModel: PlayViewModel = koin.get()
) {
    val state by viewModel.gameState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val gameAction = viewModel.gameAction

    if (uiState.connectionError) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Couldn't connect to the server",
                color = MaterialTheme.colors.error
            )
        }
        return
    }

    var showMessage by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize().background(Water)
    ) {
        LaunchedEffect(state.turnState.playerAtTurn) {
            if (state.turnState.playerAtTurn == currentPlayer.name)
                playAudio(Sound.YOUR_TURN)
        }

        LaunchedEffect(uiState.alert) {
            if (uiState.alert == null) return@LaunchedEffect

            showMessage = true
            playAudio(Sound.POPUP_ENTER)
            delay(4000)
            playAudio(Sound.POPUP_EXIT)
            showMessage = false
        }
    }

    var scale by remember { mutableStateOf(1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onPointerEvent(PointerEventType.Scroll) {
                if (it.changes.first().scrollDelta.y < 0 && scale < 2f)
                    scale += .1f
                else if (scale > 0.5f)
                    scale -= .1f
            }
    ) {
        var fieldOffset by remember { mutableStateOf(Offset.Zero) }

        LazyColumn(
            modifier = Modifier
                .height((state.size * 96 * scale).dp)
                .align(Alignment.Center)
                .offset {
                    IntOffset(fieldOffset.x.toInt(), fieldOffset.y.toInt())
                }
                .onDrag(
                    matcher = PointerMatcher.pointer(PointerType.Mouse, button = PointerButton.Secondary)
                ) {
                    fieldOffset += it
                },
        ) {
            items(state.fields) { row ->
                Row(
                    modifier = Modifier.height((96 * scale).dp)
                ) {
                    row.forEach { field ->
                        val selected = field.x == (uiState.selectedField?.x ?: -1) &&
                                field.y == (uiState.selectedField?.y ?: -1)
                        FieldCard(
                            state = field.createBlockState(
                                state.fields,
                                selected,
                                uiState.highlight,
                                currentPlayer.color,
                                scale,
                                onSelect = { gameAction.selectField(it) },
                                onUnselect = { gameAction.unselectField() }
                            )
                        )
                    }
                }
            }
        }

        uiState.selectedField.let {
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.TopEnd),
                visible = it != null,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                RightBoard(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight()
                        .menu(),
                    fieldLambda = { it ?: Field() },
                    gameAction = gameAction,
                    yourTurn = state.turnState.playerAtTurn == currentPlayer.name,
                )
            }
        }
        state.connectedPlayers[currentPlayer.name]?.let {
            LeftBoard(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .width(320.dp)
                    .fillMaxHeight()
                    .menu(),
                gameAction = gameAction,
                playerLambda = { it },
                highlight = uiState.highlight,
                playerAtTurn = state.turnState.playerAtTurn ?: "null",
                turn = state.turnState.turn,
                players = { state.connectedPlayers.values.toList() }
            )
        }

        var turn by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(state.turnState.playerAtTurn) {
            turn = if (state.turnState.playerAtTurn != currentPlayer.name) state.turnState.playerAtTurn else "Ваш ход"
            delay(2000)
            turn = null
        }

        state.turnState.playerAtTurn?.let {
            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.Center),
                visible = !turn.isNullOrEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(.75f))
                        .padding(24.dp)
                ) {
                    val formatted = if (it == currentPlayer.name) "Ваш ход" else "Ходит $it"
                    Text(text = formatted, fontSize = 18.sp)
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp),
            visible = showMessage,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(.75f))
                    .padding(24.dp)
            ) {
                Text(text = uiState.alert!!.message, fontSize = 18.sp)
            }
        }

        if (uiState.baseSelectMode) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(.75f))
                .onClick {  }
            ) {
                Box(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    BitmapImage(
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .onClick {
                                gameAction.toggleBaseSelectMode()
                            },
                        resourcePath = Texture.CLOSE
                    )
                    LazyColumn(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp)
                    ) {
                        items(state.fields) { subFields ->
                            LazyRow {
                                items(subFields) { field ->
                                    MiniField(
                                        modifier = Modifier.size(80.dp).padding(2.dp),
                                        field = { field },
                                        onSelect = {
                                            gameAction.toggleBaseSelectMode()
                                            gameAction.attackFrom(field.x, field.y)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (currentPlayer.name == state.turnState.playerAtTurn && state.connectedPlayers.size > 1) {
            var time by remember { mutableStateOf(0) }
            LaunchedEffect(state.turnState.playerAtTurn) {
                time = 0
                repeat(34) {
                    delay(1000)
                    if (it < 33) {
                        time = it + 1
                    } else {
                        gameAction.skipTurn()
                    }
                }
            }

            AnimatedVisibility(
                modifier = Modifier.align(Alignment.TopCenter),
                visible = currentPlayer.name == state.turnState.playerAtTurn,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .background(Color.Black.copy(.5f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BitmapImage(
                        modifier = Modifier.size(44.dp),
                        resourcePath = "ui/timer/timer_$time.png"
                    )
                    Text(
                        text = "Ваш ход",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LeftBoard(
    modifier: Modifier = Modifier,
    gameAction: GameAction,
    highlight: Boolean = false,
    playerLambda: () -> Player,
    playerAtTurn: String,
    turn: Int,
    players: () -> List<Player>
) {
    playerLambda().let { player ->
        Box(
            modifier
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart).padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = player.name, fontSize = 24.sp)
                    Box(
                        Modifier
                            .background(player.color)
                            .size(20.dp)
                    )
                    BitmapSquareButton(
                        modifier = Modifier.size(36.dp),
                        texture = Texture.HIGHLIGHT_ON,
                        lockedTexture = Texture.HIGHLIGHT_OFF,
                        locked = !highlight,
                        onClick = {
                            gameAction.toggleHighlight()
                        }
                    )
                }
                Divider(Modifier.fillMaxWidth().height(4.dp).background(Color.DarkGray))
                Text("Ходит: $playerAtTurn $turn/3", fontSize = 20.sp)
                Spacer(Modifier.height(32.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BitmapImage(
                        modifier = Modifier.size(32.dp),
                        resourcePath = Texture.PLAYERS
                    )
                    Text("Игроки", color = Color.White.copy(.45f))
                }
                LazyColumn(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                ) {
                    items(players()) { opponent ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(opponent.color)
                            )
                            Splash(
                                tooltip = {
                                    ResourceItem(
                                        modifier = Modifier.size(48.dp).offset(0.dp, (-8).dp),
                                        count = opponent.resources.find { it.type == Resource.Type.GOLD }?.count ?: 0,
                                        resourcePath = "resource/gold.png"
                                    )
                                }
                            ) {
                                Text(text = "${opponent.name} : ${opponent.fields}${if (opponent.disconnected) " (Вышел)" else ""}", fontSize = 18.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
                Text("Инвентарь")
                LazyVerticalGrid(
                    modifier = Modifier.background(Color.DarkGray),
                    columns = GridCells.Adaptive(120.dp),
                    contentPadding = PaddingValues(
                        start = 0.dp,
                        top = 0.dp,
                        end = 0.dp,
                        bottom = 0.dp
                    ),
                ) {
                    items(player.resources) { resource ->
                        ResourceItem(
                            count = resource.count,
                            resourcePath = "resource/${resource.type.name.lowercase()}.png"
                        )
                    }
                }
                Text("Промышленность ${player.buildings}/${player.houses * 2}")
            }
        }
    }
}

@Composable
fun RightBoard(
    modifier: Modifier = Modifier,
    fieldLambda: () -> Field,
    gameAction: GameAction,
    yourTurn: Boolean,
) {
    fieldLambda().let { field ->
        Box(
            modifier
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = field.name, fontSize = 20.sp, textAlign = TextAlign.Center)
                    Text(text = field.owner ?: "Не имеет владельца", fontSize = 16.sp)
                }
                Spacer(Modifier.height(16.dp))
                Divider(Modifier.fillMaxWidth().height(4.dp).background(Color.DarkGray))
                Spacer(Modifier.height(16.dp))
                Text(text = "Ресурсы")
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    field.resources.forEach { resource ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ResourceItem(
                                modifier = Modifier.size(56.dp),
                                count = resource.count,
                                resourcePath = "resource/${resource.type.name.lowercase()}.png"
                            )
                            val locked = resource.count == 0 || !field.isCaptured || field.owner != currentPlayer.name || !yourTurn

                            BitmapSquareButton(
                                modifier = Modifier.bounceClick().size(48.dp),
                                texture = Texture.MINE_BUTTON,
                                lockedTexture = Texture.MINE_BUTTON_LOCKED,
                                locked = locked,
                                onClick = {
                                    gameAction.mine(field.x, field.y, resource.type)
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(text = "Строения ${field.buildings.size}/${field.type.slots}")
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    field.buildings.forEach { building ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BitmapImage(
                                modifier = Modifier.size(48.dp),
                                resourcePath = "buildings/${building.type.name.lowercase()}.png"
                            )
                        }
                    }
                }
                if (field.type.slots != field.buildings.size)
                LazyColumn {
                    items(Building.Type.values()) { type ->
                        if (!field.canBuild(type)) return@items

                        Spacer(Modifier.height(12.dp))
                        Box(
                            Modifier.width(350.dp),
                        ) {
                            Row(
                                Modifier.align(Alignment.CenterStart),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Splash(
                                    tooltip = {
                                        Row {
                                            type.resources.forEach { resource ->
                                                ResourceItem(
                                                    count = resource.count,
                                                    resourcePath = "resource/${resource.type.name.lowercase()}.png"
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    BitmapImage(
                                        resourcePath = "buildings/${type.name.lowercase()}.png"
                                    )
                                }
                                Text(text = type.displayName)
                            }

                            val locked = !field.isCaptured
                                    || !currentPlayer.isEnoughResourcesFor(type)
                                    || field.owner != currentPlayer.name || !yourTurn
                            BitmapSquareButton(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(48.dp)
                                    .bounceClick(),
                                texture = Texture.BUILD,
                                lockedTexture = Texture.BUILD_LOCKED,
                                locked = locked,
                                onClick = {
                                    gameAction.build(field.x, field.y, type)
                                }
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val attackLocked = !yourTurn || field.owner == currentPlayer.name || field.lastCaptured == currentPlayer.name || field.lastCaptured == null

                if (attackLocked) {
                    AnimatedVisibility(
                        modifier = Modifier.padding(bottom = 16.dp),
                        visible = field.captureProgress > 0 && field.captureProgress != 3,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Text(
                            text = "${field.captureProgress}/3"
                        )
                    }

                    val captureLocked = field.isCaptured || !yourTurn || (field.lastCaptured != null && field.lastCaptured != currentPlayer.name)

                    LargeBitmapButton(
                        texture = Texture.BUTTON,
                        lockedTexture = Texture.BUTTON_LOCKED,
                        locked = captureLocked,
                        onClick = {
                            if (!captureLocked) {
                                gameAction.capture(field.x, field.y)
                            } else {
                                playAudio(Sound.FAILED)
                            }
                        },
                        text = if (field.lastCaptured != currentPlayer.name && field.lastCaptured != null)
                            "Захватывает ${field.lastCaptured}"
                        else if (!field.isCaptured) "Захватить"
                        else if (field.owner != currentPlayer.name) "Захвачено ${field.owner}"
                        else "Захвачено"
                    )
                } else {
                    LargeBitmapButton(
                        texture = Texture.BUTTON,
                        lockedTexture = Texture.BUTTON_LOCKED,
                        locked = false,
                        onClick = {
                            gameAction.toggleBaseSelectMode()
                        },
                        text = "Напасть"
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Splash(
    modifier: Modifier = Modifier,
    tooltip: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    TooltipArea(
        tooltip = {
            Column(
                modifier = modifier
                    .background(Color.Black.copy(.75f), RectangleShape)
                    .padding(8.dp)
                    .wrapContentWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                tooltip()
            }
        },
        delayMillis = 0,
        tooltipPlacement = TooltipPlacement.ComponentRect(
            anchor = Alignment.TopCenter,
            alignment = Alignment.TopCenter,
            offset = DpOffset.Zero
        )
    ) {
        content()
    }
}