package ui

import currentPlayer
import data.GameState
import data.RealtimeMessagingClient
import ipGlobal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import models.Alert
import models.Building
import models.Field
import models.Resource
import packets.Packet
import packets.PacketType
import packets.client.*
import packets.server.*
import utils.playAudio

data class GameAction(
    val selectField: (field: Field) -> Unit,
    val unselectField: () -> Unit,
    val capture: (x: Int, y: Int) -> Unit,
    val mine: (x: Int, y: Int, resource: Resource.Type) -> Unit,
    val build: (x: Int, y: Int, building: Building.Type) -> Unit,
    val attackFrom: (x: Int, y: Int, ) -> Unit,
    val toggleBaseSelectMode: () -> Unit,
    val toggleHighlight: () -> Unit,
    val skipTurn: () -> Unit,
)

data class PlayUIState(
    val isConnecting: Boolean = false,
    val connectionError: Boolean = false,
    val selectedField: Field? = null,
    val highlight: Boolean = true,
    val baseSelectMode: Boolean = false,
    val alert: Alert? = null
)

class PlayViewModel(
    private val client: RealtimeMessagingClient,
    private val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) {
    init {
        viewModelScope.launch {
            client.getPacketListener(
                ip = ipGlobal,
                playerName = currentPlayer.name,
                rgbColor = currentPlayer.rgb.toString(),
                onPacketReceived = { packet ->
                    consumePacket(packet)
                },
                onCatchError = { exception ->
                    consumeException(exception)
                }
            )
        }
    }

    private val _uiState = MutableStateFlow(PlayUIState())
    val uiState: StateFlow<PlayUIState>
        get() = _uiState.asStateFlow()

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState>
        get() = _gameState.asStateFlow()

    private fun consumePacket(packet: Packet) = when (packet.packetType) {
        PacketType.Client.GAME_STATE -> _gameState.update { (packet as PlayClientGameState).gameState }
        PacketType.Client.SOUND -> playAudio((packet as PlayClientSound).sound)
        PacketType.Client.ALERT -> _uiState.update {
            val alertPacket = packet as PlayClientAlertMessage
            it.copy(alert = Alert(alertPacket.message, alertPacket.sound))
        }
        PacketType.Client.FIELD_DATA_CHANGE -> {
            val field = (packet as PlayClientFieldDataChange).field

            if (_uiState.value.selectedField?.x == field.x && _uiState.value.selectedField?.y == field.y)
                _uiState.update { it.copy(selectedField = field) }

            _gameState.update {
                it.copy(
                    fields = it.fields.apply { this[field.y][field.x] = field }
                )
            }
        }
        PacketType.Client.TURN_STATE -> _gameState.update { it.copy(turnState = (packet as PlayClientTurnState).turnState) }
        PacketType.Client.PLAYER_DATA_CHANGE -> _gameState.update {
            val player = (packet as PlayClientPlayerDataChange).player
            it.copy(
                connectedPlayers = it.connectedPlayers.apply {
                    this[player.name] = player
                }
            )
        }
        else -> {}
    }

    private fun consumeException(exception: Exception) {
        exception.printStackTrace()
    }

    val gameAction = GameAction(
        selectField = { field ->
            _uiState.update { it.copy(selectedField = field) }
        },
        unselectField = {
            _uiState.update { it.copy(selectedField = null) }
        },
        capture = { x, y ->
            val packet = PlayServerCaptureField(x, y)
            sendPacket(packet)
        },
        mine = { x, y, resource ->
            val packet = PlayServerMineResource(x, y, resource)
            sendPacket(packet)
        },
        build = { x, y, building ->
            val packet = PlayServerBuild(x, y, building)
            sendPacket(packet)
        },
        attackFrom = { x, y ->
            _uiState.value.selectedField?.let {
                val packet = PlayServerAttack(
                    fromX = x,
                    fromY = y,
                    attackedX = it.x,
                    attackedY = it.y
                )
                sendPacket(packet)
            }
        },
        toggleBaseSelectMode = {
            _uiState.update { it.copy(baseSelectMode = !_uiState.value.baseSelectMode) }
        },
        toggleHighlight = {
            _uiState.update { it.copy(highlight = !_uiState.value.highlight) }
        },
        skipTurn = {
            sendPacket(PlayServerIdle())
        }
    )

    private fun sendPacket(packet: Packet) {
        viewModelScope.launch {
            client.sendPacket(packet)
        }
    }
}