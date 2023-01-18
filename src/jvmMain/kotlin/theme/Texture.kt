package theme

import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import models.Field

object Texture {
    private const val UI = "ui"
    const val PLAYERS = "$UI/players.png"
    const val CLOSE = "$UI/close.png"
    const val BUTTON = "$UI/button.png"
    const val BUTTON_LOCKED = "$UI/button_locked.png"
    const val MINE_BUTTON = "$UI/mine.png"
    const val MINE_BUTTON_LOCKED = "$UI/mine_locked.png"
    const val BUILD = "$UI/build.png"
    const val BUILD_LOCKED = "$UI/build_locked.png"
    const val UPGRADE_BUTTON = "$UI/upgrade.png"
    const val UPGRADE_BUTTON_LOCKED = "$UI/upgrade_locked.png"
    const val HIGHLIGHT_ON = "$UI/highlight_on.png"
    const val HIGHLIGHT_OFF = "$UI/highlight_off.png"

    val fields = hashMapOf(
        Pair(Field.Type.WATER, useResource("water.png") { loadImageBitmap(it) }),
        Pair(Field.Type.DESERT, useResource("desert.png") { loadImageBitmap(it) }),
        Pair(Field.Type.PLAINS, useResource("plains.png") { loadImageBitmap(it) }),
        Pair(Field.Type.SWAMP, useResource("swamp.png") { loadImageBitmap(it) }),
        Pair(Field.Type.ISLAND, useResource("island.png") { loadImageBitmap(it) }),
        Pair(Field.Type.MESA, useResource("mesa.png") { loadImageBitmap(it) }),
    )
    val MenuTop = useResource("$UI/menu_top.png") { loadImageBitmap(it) }
    val MenuSized = useResource("$UI/menu_sized.png") { loadImageBitmap(it) }
    val MenuBottom = useResource("$UI/menu_bottom.png") { loadImageBitmap(it) }
    val RedSand = useResource("red_sand.png") { loadImageBitmap(it) }
    val SwampGrass = useResource("swamp_grass.png") { loadImageBitmap(it) }
    val WaterSwamp = useResource("water_swamp.png") { loadImageBitmap(it) }
    val WaterSand = useResource("water_sand.png") { loadImageBitmap(it) }
    val WaterRedSand = useResource("water_red_sand.png") { loadImageBitmap(it) }
    val WaterGrass = useResource("water_grass.png") { loadImageBitmap(it) }
    val Grass = useResource("grass.png") { loadImageBitmap(it) }
    val Trees = useResource("trees.png") { loadImageBitmap(it) }
    val Rocks = useResource("rocks.png") { loadImageBitmap(it) }
}