package io.elven

import io.elven.anilist.Anilist
import io.elven.download.Downloader
import io.elven.download.DownloaderSettings
import io.elven.filewatch.NativeInotify
import io.elven.settings.AnileafInternalData
import io.elven.settings.DataFileHandler
import java.io.File
import java.lang.Thread.sleep
import kotlin.concurrent.fixedRateTimer

class Daemon(basePath: String? = null) {

    private val settings = DataFileHandler.load("settings.json", DownloaderSettings(), basePath)
    private val anilist = Anilist(settings, AnileafInternalData(basePath))
    private val isDebug: Boolean = System.getenv("ANILEAF_DEBUG")?.toBoolean() ?: false

    private val downloader = Downloader(settings)

    fun run() {
        val dir = File("${settings.pathToAnimes}/Dr. STONE/")
        val seconds = 20
        if (!dir.isDirectory) {
            throw Exception("not a directory")
        }
       NativeInotify()
        println("END")
    }

    fun run2() {
        println("RUN SYNC")
        val currentList = anilist.sync()
        downloader.downloadMatchingTorrents(currentList)
    }
}

fun main (args : Array<String>) {

    val daemon = Daemon(args.getOrNull(0))
    daemon.run2()
    daemon.run()
}