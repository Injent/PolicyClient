package packets.client

import kotlinx.serialization.Serializable
import models.TurnState
import packets.Packet
import packets.PacketType.Client.TURN_STATE

@Serializable
data class PlayClientTurnState(
    val turnState: TurnState
) : Packet(TURN_STATE)
