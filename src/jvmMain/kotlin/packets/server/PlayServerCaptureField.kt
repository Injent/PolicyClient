package packets.server

import kotlinx.serialization.Serializable
import packets.Packet
import packets.PacketType.Server.CAPTURE_FIELD

@Serializable
data class PlayServerCaptureField(
    val x: Int, val y: Int
) : Packet(CAPTURE_FIELD)
