package utils

import com.goxr3plus.streamplayer.stream.StreamPlayer
import java.io.File

class MediaPlayer : StreamPlayer(noLoggingLogger) {

    fun playAudio(file: File) {
        open(file)
        play()
    }
}