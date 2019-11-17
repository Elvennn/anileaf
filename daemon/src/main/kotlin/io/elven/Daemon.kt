package io.elven

import com.den_4.inotify_java.EventQueueFull
import com.den_4.inotify_java.Inotify
import com.den_4.inotify_java.InotifyEvent
import com.den_4.inotify_java.InotifyEventListener
import com.den_4.inotify_java.enums.Event
import io.elven.anilist.Anilist
import io.elven.download.Downloader
import io.elven.download.DownloaderSettings
import io.elven.settings.AnileafInternalData
import io.elven.settings.DataFileHandler
import java.io.File
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.fixedRateTimer

class Daemon(basePath: String? = null) {

    private val settings = DataFileHandler.load("settings.json", DownloaderSettings(), basePath)
    private val anilist = Anilist(settings, AnileafInternalData(basePath))
    private val isDebug: Boolean = System.getenv("ANILEAF_DEBUG")?.toBoolean() ?: false

    private val downloader = Downloader(settings)

    fun run() {
        fixedRateTimer(period = settings.syncFrequency.toLong() * 1000) {
            println("RUN SYNC")
            val currentList = anilist.sync()
            downloader.downloadMatchingTorrents(currentList)
        }
    }

}

fun main (args : Array<String>) {
    Daemon(args.getOrNull(0)).run()
}