package io.elven.download

import io.elven.settings.GlobalSettings

class DownloaderSettings(
    anilistUserName: String = "",
    anilistToken: String = "",
    logLevel: String = "DEBUG",
    logPath: String = "",
    val syncFrequency: Int = 3000,
    val pathToAnimes: String = "",
    val pathToDownloadAnimes: String = "",
    val torrentRSSFeed: String = "",
    val transmissionURL: String = "",
    val transmissionCredentials: String = "",
    val freeSmsNotificationUser: String = "",
    val freeSmsNotificationPassword: String = "",
    val minVideoQuality: Array<String> = emptyArray(),
    val animeSettings: Array<AnimeSettings> = emptyArray()
) : GlobalSettings(anilistUserName, anilistToken, logLevel, logPath) {
}

class AnimeSettings(
    val aniID: Int,
    val titleVariants: Array<String> = emptyArray(),
    val prefFasubTeam: String
)