package packets.client

import kotlinx.serialization.Serializable
import models.Player
import packets.Packet
import packets.PacketType.Client.FIELD_DATA_CHANGE

@Serializable
data class PlayClientPlayerDataChange(
    val player: Player
) : Packet(FIELD_DATA_CHANGE)
