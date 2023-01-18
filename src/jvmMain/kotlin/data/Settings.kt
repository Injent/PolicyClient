package data

import java.io.File
import java.util.*

class Settings(val file: File) {
    private val properties: Properties = Properties()
    var soundVolume: Double = 1.0
        set(value) {
            field = value
            properties.setProperty("sound_volume", value.toString())
        }

    init {
        if (!file.exists())
            file.createNewFile()
        properties.load(file.reader())
        soundVolume = properties.getProperty("sound_volume").toDoubleOrNull() ?: 1.0
    }

    fun save() {
        if (!file.exists())
            file.createNewFile()
        properties.store(file.outputStream(), null)
    }
}