package packets.server

import kotlinx.serialization.Serializable
import packets.Packet
import packets.PacketType.Server.IDLE

@Serializable
class PlayServerIdle : Packet(IDLE)
