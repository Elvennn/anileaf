package io.elven

import io.elven.anilist.Anilist
import io.elven.download.Downloader
import io.elven.download.DownloaderSettings
import io.elven.filewatch.AnimeWatch
import io.elven.settings.AnileafInternalData
import io.elven.settings.DataFileHandler
import kotlin.concurrent.fixedRateTimer

class Daemon(basePath: String? = null) {
    private val settings = DataFileHandler.load("settings.json", DownloaderSettings(), basePath)
    private val anilist = Anilist(settings, AnileafInternalData(basePath))

    private val downloader = Downloader(settings)

    fun run() {
        println("Daemon Started")
        val fileWatch = AnimeWatch(settings.pathToAnimeWatchExec, anilist, settings.pathToAnimes)
        fixedRateTimer(period = settings.syncFrequency.toLong() * 1000) {
            val currentList = anilist.sync()
            fileWatch.start(currentList)
            downloader.downloadMatchingTorrents(currentList, exactness = 90)
        }
    }
}

fun main(args: Array<String>) {
    Daemon(args.getOrNull(0)).run()
}