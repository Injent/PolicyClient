package packets.server

import kotlinx.serialization.Serializable
import models.Building
import packets.Packet
import packets.PacketType.Server.BUILD

@Serializable
data class PlayServerBuild(
    val x: Int,
    val y: Int,
    val buildingType: Building.Type
) : Packet(BUILD)
