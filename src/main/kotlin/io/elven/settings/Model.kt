package io.elven.settings

data class Settings(
    val anilistToken: String,
    val syncFrequency: Double = 3000.0,
    val pathToAnimes: String = "",
    val pathToTorrentFiles: String = "",
    val torrentRSSFeed: String = "",
    val minVideoQuality: String = "",
    val animeSettings: Array<AnimeSettings> = emptyArray()
)

data class AnimeSettings(
    val aniID: Int,
    val titleVariants: Array<String> = emptyArray(),
    val prefFasubTeam: String
)