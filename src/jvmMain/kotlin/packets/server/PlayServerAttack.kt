package packets.server

import kotlinx.serialization.Serializable
import packets.Packet
import packets.PacketType.Server.ATTACK

@Serializable
data class PlayServerAttack(
    val fromX: Int,
    val fromY: Int,
    val attackedX: Int,
    val attackedY: Int
) : Packet(ATTACK)