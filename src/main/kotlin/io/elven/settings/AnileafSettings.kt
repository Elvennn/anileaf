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
            File(path).mkdir()
            settingsFile.writeText(mapper.writeValueAsString(Settings("", "")))
        }
        settings = mapper.readValue<Settings>(settingsFile.readText())

        val torrentDir = File(settings.pathToTorrentFiles)
        if (!torrentDir.exists()) {
            torrentDir.mkdir()
        }
    }
    fun save () {
        settingsFile.writeText(mapper.writeValueAsString(settings))
    }
}

data class Settings(
    var anilistUserName: String,
    var anilistToken: String,
    val syncFrequency: Int = 3000,
    val pathToAnimes: String = "",
    val pathToTorrentFiles: String = "${AnileafSettings.path}/torrentFiles",
    val torrentRSSFeed: String = "",
    val minVideoQuality: String = "",
    val animeSettings: Array<AnimeSettings> = emptyArray()
)

data class AnimeSettings(
    val aniID: Int,
    val titleVariants: Array<String> = emptyArray(),
    val prefFasubTeam: String
)