package ui.menu

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.useResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import models.CustomColor

data class JoinUIState(
    val color: Color = Color.White,
    val colors: List<Color> = emptyList(),
)

class JoinViewModel(
    private val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) {
    private val _uiState = MutableStateFlow(JoinUIState())
    val uiState: StateFlow<JoinUIState>
        get() = _uiState.asStateFlow()

    init {
        loadColors()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadColors() {
        val customColors = useResource(resourcePath = "colors.json") { Json.decodeFromStream<List<CustomColor>>(it) }
        val colors = customColors.map {
            it.toComposeColor()
        }
        _uiState.update {
            it.copy(colors = colors)
        }
    }

    fun selectColor(color: Color) {
        _uiState.update { it.copy(color = color) }
    }
}