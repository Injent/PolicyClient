package packets.client

import data.GameState
import kotlinx.serialization.Serializable
import packets.Packet
import packets.PacketType.Client.GAME_STATE

@Serializable
data class PlayClientGameState(
    val gameState: GameState
) : Packet(GAME_STATE)
