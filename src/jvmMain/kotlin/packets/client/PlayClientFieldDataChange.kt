package packets.client

import kotlinx.serialization.Serializable
import models.Field
import packets.Packet
import packets.PacketType.Client.FIELD_DATA_CHANGE

@Serializable
data class PlayClientFieldDataChange(
    val field: Field
) : Packet(FIELD_DATA_CHANGE)
