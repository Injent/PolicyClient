package data

import kotlinx.serialization.Serializable
import models.Field
import models.Player
import models.TurnState
import kotlin.random.Random

@Serializable
data class GameState(
    val seed: Long = Random.nextLong(),
    val size: Int = 1,
    val fields: Array<Array<Field>> = emptyArray(),
    val connectedPlayers: HashMap<String, Player> = HashMap(),
    val turnState: TurnState = TurnState()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameState

        if (seed != other.seed) return false
        if (size != other.size) return false
        if (!fields.contentDeepEquals(other.fields)) return false
        if (connectedPlayers != other.connectedPlayers) return false
        if (turnState != other.turnState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = seed.hashCode()
        result = 31 * result + size
        result = 31 * result + fields.contentDeepHashCode()
        result = 31 * result + connectedPlayers.hashCode()
        result = 31 * result + turnState.hashCode()
        return result
    }
}