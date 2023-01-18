package utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

//@Composable
//fun AsyncBitmapImage(
//    modifier: Modifier = Modifier,
//    size: Int = 1,
//    resourcePath: String,
//) {
//    val bitmap by produceState<ImageBitmap?>(null) {
//        value = useResource(resourcePath) {
//            loadImageBitmap(it)
//        }
//    }
//
//    bitmap?.let {
//        BitmapImageResized(
//            modifier, size, it,
//        )
//    }
//}

@Composable
fun BitmapImage(
    modifier: Modifier = Modifier.size(48.dp),
    bitmap: ImageBitmap,
    colorFilter: ColorFilter? = null
) {
    BitmapImageResized(
        modifier, bitmap, colorFilter
    )
}

@Composable
fun BitmapImage(
    modifier: Modifier = Modifier.size(48.dp),
    resourcePath: String,
    colorFilter: ColorFilter? = null
) {
    val bitmap = useResource(resourcePath) {
        loadImageBitmap(it)
    }
    BitmapImageResized(
        modifier, bitmap, colorFilter
    )
}

@Composable
private fun BitmapImageResized(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap,
    colorFilter: ColorFilter? = null
) {
    Canvas(
        modifier
    ) {
        drawImage(
            image = bitmap,
            dstSize = IntSize(this.size.width.toInt(), this.size.height.toInt()),
            filterQuality = FilterQuality.None,
            colorFilter = colorFilter
        )
    }
}