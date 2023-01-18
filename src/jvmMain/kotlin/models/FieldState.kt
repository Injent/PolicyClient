package models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class FieldState(
    val west: SideState?,
    val north: SideState?,
    val east: SideState?,
    val south: SideState?,
    val selected: Boolean = false,
    val highlight: Boolean = false,
    val fieldColor: Color = Color.Transparent,
    val scale: Float = 1.0f,
    val field: Field,
    val onSelect: (field: Field) -> Unit,
    val onUnselect: () -> Unit
) {
    val states = listOf(west, north, east, south)

    @Immutable
    data class SideState(
        val owner: String?,
        val type: Field.Type?
    )
}
