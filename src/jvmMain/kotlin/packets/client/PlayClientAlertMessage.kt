package packets.client

import kotlinx.serialization.Serializable
import packets.Packet
import packets.PacketType.Client.ALERT
import theme.Sound.POPUP_ENTER

@Serializable
data class PlayClientAlertMessage(
    val message: String,
    val sound: String? = POPUP_ENTER
) : Packet(ALERT)