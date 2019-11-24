package io.elven

import io.elven.anilist.Anilist
import io.elven.download.Downloader
import io.elven.download.DownloaderSettings
import io.elven.filewatch.FileWatch
import io.elven.settings.AnileafInternalData
import io.elven.settings.DataFileHandler
import kotlin.concurrent.fixedRateTimer

class Daemon(basePath: String? = null) {

    private val settings = DataFileHandler.load("settings.json", DownloaderSettings(), basePath)
    private val anilist = Anilist(settings, AnileafInternalData(basePath))
    private val isDebug: Boolean = System.getenv("ANILEAF_DEBUG")?.toBoolean() ?: false

    private val downloader = Downloader(settings)

    fun run() {
        println("Daemon Started")
        FileWatch.start(anilist, settings)
        fixedRateTimer(period = settings.syncFrequency.toLong() * 1000) {
            val currentList = anilist.sync()
            downloader.downloadMatchingTorrents(currentList)
        }
    }

}

fun main(args: Array<String>) {
    Daemon(args.getOrNull(0)).run()
}