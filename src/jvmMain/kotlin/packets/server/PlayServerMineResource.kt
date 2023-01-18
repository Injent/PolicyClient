package packets.server

import kotlinx.serialization.Serializable
import models.Resource
import packets.Packet
import packets.PacketType.Server.MINE

@Serializable
data class PlayServerMineResource(
    val x: Int,
    val y: Int,
    val resource: Resource.Type
) : Packet(MINE)
