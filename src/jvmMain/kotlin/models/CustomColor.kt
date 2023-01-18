package models

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class CustomColor(
    val name: String,
    val rgb: Array<Int>
) {
    fun toComposeColor() = Color(rgb[0], rgb[1], rgb[2])

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomColor

        if (name != other.name) return false
        if (!rgb.contentEquals(other.rgb)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + rgb.contentHashCode()
        return result
    }
}