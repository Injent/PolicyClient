package utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.ClassLoaderResourceLoader
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.io.File
import java.util.logging.Logger

@OptIn(ExperimentalSerializationApi::class)
val jsonFormat = Json { explicitNulls = false }

fun String.extractParam(param: String): String {
    return substringAfter("$param=").substringBefore(";")
}

fun Modifier.conditional(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

enum class ButtonState { Pressed, Idle }
fun Modifier.bounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0.90f else 1f)

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {  }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

fun DrawScope.drawBitmap(bitmap: ImageBitmap, rotation: Float = 0f, size: IntSize) {
    rotate(rotation) {
        drawImage(
            image = bitmap,
            dstSize = size,
            filterQuality = FilterQuality.None,
        )
    }
}

fun playAudio(resourcePath: String, scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) {
    scope.launch {
        val file = getResource(resourcePath)
        val player = MediaPlayer()
        player.setGain(-2.0)
        player.playAudio(file)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun getResource(resourcePath: String): File {
    val contextClassLoader = Thread.currentThread().contextClassLoader!!
    val url = contextClassLoader.getResource(resourcePath)
        ?: (::ClassLoaderResourceLoader.javaClass).getResource(resourcePath)
    return File(url.toURI())
}

val noLoggingLogger: Logger
    get() {
        val logger: Logger = Logger.getLogger("NoLoggingLogger")
        logger.useParentHandlers = false
        for (handler in logger.handlers) {
            logger.removeHandler(handler)
        }
        return logger
    }