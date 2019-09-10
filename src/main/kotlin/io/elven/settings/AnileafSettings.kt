package io.elven.settings

import com.fasterxml.jackson.databind.SerializationFeature
import java.io.File
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object AnileafSettings {
    private val path: String = System.getProperty("user.home")
    private val fileName: String = ".anileaf/settings.json"
    private val settingsFile = File("$path/$fileName")
    private val mapper = jacksonObjectMapper()
    val settings = load()

    init {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
    }

    private fun load() = mapper.readValue<Settings>(settingsFile.readText())

    // fun save() = settingsFile.writeText(mapper.writeValueAsString(settings))

}

data class Settings(
    val anilistToken: String,
    val syncFrequency: Int = 3000,
    val pathToAnimes: String = "",
    val pathToTorrentFiles: String = "",
    val torrentRSSFeed: String = "",
    val minVideoQuality: String = "",
    val animeSettings: Array<AnimeSettings> = emptyArray()
) {

    companion object {

    }
}

data class AnimeSettings(
    val aniID: Int,
    val titleVariants: Array<String> = emptyArray(),
    val prefFasubTeam: String
)