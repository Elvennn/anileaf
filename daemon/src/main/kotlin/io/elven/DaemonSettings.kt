package io.elven

import io.elven.settings.GlobalSettings

class DaemonSettings(
    anilistUserName: String = "",
    anilistToken: String = "",
    val syncFrequency: Int = 3000,
    val pathToAnimes: String = "",
    val torrentRSSFeed: String = "",
    val transmissionURL: String = "",
    val transmissionCredentials: String = "",
    val minVideoQuality: Array<String> = emptyArray(),
    val animeSettings: Array<AnimeSettings> = emptyArray()
) : GlobalSettings(anilistUserName, anilistToken)

class AnimeSettings(
    val aniID: Int,
    val titleVariants: Array<String> = emptyArray(),
    val prefFasubTeam: String
)