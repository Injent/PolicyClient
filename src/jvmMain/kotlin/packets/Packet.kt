package packets

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import packets.client.*
import packets.server.*

@Serializable
abstract class Packet(@SerialName("packetId") val packetType: Int)

object PacketType {
    object Server {
        const val CAPTURE_FIELD = 0
        const val MINE = 1
        const val BUILD = 2
        const val IDLE = 3
        const val ATTACK = 4
    }

    object Client {
        const val GAME_STATE = 0
        const val ALERT = 1
        const val SOUND = 2
        const val FIELD_DATA_CHANGE = 3
        const val TURN_STATE = 4
        const val PLAYER_DATA_CHANGE = 5
    }
}

object ServerPacketSerializer : JsonContentPolymorphicSerializer<Packet>(Packet::class) {
    override fun selectDeserializer(element: JsonElement): KSerializer<out Packet> {
        return when (element.jsonObject["packetId"].toString().toIntOrNull()) {
            PacketType.Server.IDLE -> PlayServerIdle.serializer()
            PacketType.Server.CAPTURE_FIELD -> PlayServerCaptureField.serializer()
            PacketType.Server.MINE -> PlayServerMineResource.serializer()
            PacketType.Server.BUILD -> PlayServerBuild.serializer()
            PacketType.Server.ATTACK -> PlayServerAttack.serializer()
            else -> Packet.serializer()
        }
    }
}

object ClientPacketSerializer : JsonContentPolymorphicSerializer<Packet>(Packet::class) {
    override fun selectDeserializer(element: JsonElement): KSerializer<out Packet> {
        return when (element.jsonObject["packetId"].toString().toIntOrNull()) {
            PacketType.Client.GAME_STATE -> PlayClientGameState.serializer()
            PacketType.Client.SOUND -> PlayClientSound.serializer()
            PacketType.Client.ALERT -> PlayClientAlertMessage.serializer()
            PacketType.Client.FIELD_DATA_CHANGE -> PlayClientFieldDataChange.serializer()
            PacketType.Client.TURN_STATE -> PlayClientTurnState.serializer()
            PacketType.Client.PLAYER_DATA_CHANGE -> PlayClientPlayerDataChange.serializer()
            else -> Packet.serializer()
        }
    }
}