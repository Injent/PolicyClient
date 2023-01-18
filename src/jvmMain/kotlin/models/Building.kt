package models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Building(
    val type: Type = Type.NONE
) {
    @Serializable(with = Type.BuildingSerializer::class)
    enum class Type(val resources: List<Resource> = emptyList()) {
        NONE,
        SAWMILL(
            listOf(
                Resource(2, Resource.Type.WOOD),
                Resource(4, Resource.Type.STONE)
            )
        ),
        MINESHAFT(
            listOf(
                Resource(4, Resource.Type.WOOD),
                Resource(2, Resource.Type.STONE)
            )
        ),
        HOUSE(
            listOf(
                Resource(5, Resource.Type.WOOD),
            )
        ),
        LABORATORY(
            listOf(
                Resource(3, Resource.Type.STONE),
                Resource(2, Resource.Type.IRON)
            )
        ),
        BANK(
            listOf(
                Resource(3, Resource.Type.STONE),
                Resource(3, Resource.Type.IRON),
                Resource(5, Resource.Type.GOLD)
            )
        ),
        MILITARY_BASE(
            listOf(
                Resource(3, Resource.Type.IRON),
                Resource(4, Resource.Type.STONE)
            )
        ),
        FORT(
            listOf(
                Resource(6, Resource.Type.STONE),
            )
        );

        val displayName: String
            get() {
                return when (this) {
                    SAWMILL -> "Лесопилка"
                    MINESHAFT -> "Шахта"
                    BANK -> "Банк"
                    LABORATORY -> "Лаборатория"
                    HOUSE -> "Жилой дом"
                    MILITARY_BASE -> "Военная база"
                    FORT -> "Форт"
                    else -> "Пусто"
                }
            }

        object BuildingSerializer : KSerializer<Type> {
            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Type", PrimitiveKind.STRING)
            override fun serialize(encoder: Encoder, value: Type) {
                encoder.encodeString(value.toString().lowercase())
            }
            override fun deserialize(decoder: Decoder): Type {
                return Type.valueOf(decoder.decodeString().uppercase())
            }
        }
    }
}
