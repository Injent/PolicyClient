package models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Resource(
    val count: Int = 0,
    val type: Type = Type.NONE
) {

    companion object {
        fun startResources(): List<Resource> {
            val list = mutableListOf<Resource>()
            Type.values().forEach { type ->
                when (type) {
                    Type.WOOD -> list.add(Resource(type = type, count = 20))
                    Type.STONE -> list.add(Resource(type = type, count = 20))
                    Type.IRON -> list.add(Resource(type = Type.IRON, count = 5))
                    Type.NONE -> {}
                    else -> { list.add(Resource(type = type)) }
                }
            }
            return list
        }
    }

    @Serializable(with = Type.ResourceSerializer::class)
    enum class Type(val maxCount: Int = 20) {
        NONE(0),
        WOOD(60),
        STONE(60),
        COAL(40),
        IRON(40),
        CRYSTAL(14),
        GOLD(20);

        object ResourceSerializer : KSerializer<Type> {
            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Action", PrimitiveKind.STRING)
            override fun serialize(encoder: Encoder, value: Type) {
                encoder.encodeString(value.toString().lowercase())
            }
            override fun deserialize(decoder: Decoder): Type {
                return Type.valueOf(decoder.decodeString().uppercase())
            }
        }
    }
}
