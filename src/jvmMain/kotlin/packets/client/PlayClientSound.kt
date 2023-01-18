package packets.client

import kotlinx.serialization.Serializable
import packets.Packet
import packets.PacketType.Client.SOUND

@Serializable
data class PlayClientSound(
    val sound: String
) : Packet(SOUND)
