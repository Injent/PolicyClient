package models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
@Immutable
data class Field(
    val x: Int = -1,
    val y: Int = -1,
    val name: String = "Unnamed",
    val captureProgress: Int = 0,
    val type: Type = Type.PLAINS,
    val owner: String? = null,
    val lastCaptured: String? = null,
    val resources: List<Resource> = emptyList(),
    val buildings: List<Building> = emptyList(),
) {
    val isCaptured
        get() = captureProgress >= 3

    fun canBuild(building: Building.Type): Boolean {
        return if (building == Building.Type.NONE)
            false
        else if (buildings.contains(Building(Building.Type.MILITARY_BASE)))
            false
        else if (building == Building.Type.MILITARY_BASE && buildings.isNotEmpty())
            false
        else if (buildings.any { it.type == building && it.type != Building.Type.HOUSE })
            false
        else if (building == Building.Type.HOUSE && type == Type.DESERT)
            false
        else !(building == Building.Type.SAWMILL && (type == Type.MOUNTAINS || type == Type.DESERT || type == Type.ISLAND || type == Type.SWAMP))
    }

    fun createBlockState(
        fields: Array<Array<Field>>,
        selected: Boolean,
        highlight: Boolean,
        fieldColor: Color,
        scale: Float,
        onSelect: (field: Field) -> Unit,
        onUnselect: () -> Unit
    ): FieldState {
        val positions = listOf(Pair(-1, 0), Pair(0, -1), Pair(1, 0), Pair(0, 1))

        val sideStates = positions.map { pos ->
            try {
                val field = fields[y + (pos.second)][x + (pos.first)]
                FieldState.SideState(field.owner, field.type)
            } catch (_: IndexOutOfBoundsException) {
                null
            }
        }

        return FieldState(
            west = sideStates[0],
            north = sideStates[1],
            east = sideStates[2],
            south = sideStates[3],
            selected = selected,
            highlight = highlight,
            fieldColor = fieldColor,
            scale = scale,
            field = this,
            onSelect = onSelect,
            onUnselect = onUnselect
        )
    }

    @Serializable(with = Type.FieldSerializer::class)
    enum class Type(val resources: List<Resource.Type>, val slots: Int = 3, val weight: Float = 1f) {
        WATER(emptyList(), 0, .05f),
        PLAINS(listOf(Resource.Type.WOOD, Resource.Type.STONE, Resource.Type.COAL), 4, .2f),
        FOREST(listOf(Resource.Type.WOOD, Resource.Type.STONE, Resource.Type.IRON), 3, .2f),
        DESERT(listOf(Resource.Type.IRON, Resource.Type.GOLD), 3, .2f),
        MOUNTAINS(listOf(Resource.Type.COAL, Resource.Type.STONE, Resource.Type.IRON), 2, .2f),
        MESA(listOf(Resource.Type.GOLD, Resource.Type.WOOD), 3, .1f),
        ISLAND(listOf(Resource.Type.CRYSTAL), slots = 2, .05f),
        SWAMP(listOf(Resource.Type.COAL), 2, .05f);

        val isWithGrass: Boolean
            get() = this == MOUNTAINS || this == FOREST || this == PLAINS

        object FieldSerializer : KSerializer<Type> {
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