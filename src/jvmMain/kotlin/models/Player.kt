package models

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import models.Resource.Companion.startResources

@Serializable
data class Player(
    val name: String = "",
    val resources: List<Resource> = startResources(),
    var fields: Int = 0,
    var gold: Int = 0,
    var houses: Int = 0,
    var buildings: Int = 0,
    val rgb: Int = 0,
    val notification: String? = null,
    val disconnected: Boolean = false,
) {
    fun isEnoughResourcesFor(buildingType: Building.Type): Boolean {
        var availableResCount = 0

        buildingType.resources.forEach { requiredResource ->
            val playerResource = resources.find {
                requiredResource.type == it.type
            }
            if (requiredResource.count <= playerResource!!.count)
                availableResCount++
        }
        return availableResCount >= buildingType.resources.size
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        if (name != other.name) return false
        if (resources != other.resources) return false
        if (fields != other.fields) return false
        if (gold != other.gold) return false
        if (houses != other.houses) return false
        if (buildings != other.buildings) return false
        if (rgb != other.rgb) return false
        if (notification != other.notification) return false
        if (disconnected != other.disconnected) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + resources.hashCode()
        result = 31 * result + fields
        result = 31 * result + gold
        result = 31 * result + houses
        result = 31 * result + buildings
        result = 31 * result + rgb.hashCode()
        result = 31 * result + (notification?.hashCode() ?: 0)
        result = 31 * result + disconnected.hashCode()
        return result
    }

    val color: Color
        get() = Color(rgb)
}