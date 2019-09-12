package io.elven.settings

import com.fasterxml.jackson.databind.SerializationFeature
import java.io.File
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object AnileafSettings {
    val path: String = "${System.getProperty("user.home")}/.anileaf"
    val settings: Settings

    private const val settingsFileName: String = "settings.json"
    private val settingsFile = File("$path/$settingsFileName")
    private val mapper = jacksonObjectMapper()

    init {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        if (!settingsFile.exists()) {
            File(AnileafSettings.path).mkdir()
            settingsFile.writeText(mapper.writeValueAsString(Settings("", "")))
        }
        settings = mapper.readValue<Settings>(settingsFile.readText())
    }

}

data class Settings(
    val anilistUserName: String,
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