package models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import utils.extractParam

@Serializable
data class Callback(
    val type: Type = Type.ALERT,
    val message: String = "",
    val x: Int? = null,
    val y: Int? = null,
    val ignore: Int = 0,
) {

    val sound: String
        get() = message.extractParam("sound")
    val msg: String
        get() = message.extractParam("msg")

    @Serializable(with = Type.CallbackSerializer::class)
    enum class Type {
        ALERT,
        GLOBAL_ALERT,
        GLOBAL_SOUND,
        TURN,
        ITEM_DROP;

        object CallbackSerializer : KSerializer<Type> {
            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Callback", PrimitiveKind.STRING)
            override fun serialize(encoder: Encoder, value: Type) {
                encoder.encodeString(value.toString().lowercase())
            }
            override fun deserialize(decoder: Decoder): Type {
                return Type.valueOf(decoder.decodeString().uppercase())
            }
        }
    }
}
