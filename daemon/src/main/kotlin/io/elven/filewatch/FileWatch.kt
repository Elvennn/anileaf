package io.elven.filewatch

import io.elven.anilist.Anilist
import io.elven.anitomy.AnimeFile
import io.elven.download.DownloaderSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import kotlin.concurrent.thread

object FileWatch {
    private const val animeWatchExec = "./animewatch"

    fun start(anilist: Anilist, settings: DownloaderSettings) {
        val aniEntries = anilist.sync()
        val process =
            Runtime.getRuntime().exec(
                arrayOf(
                    animeWatchExec,
                    *aniEntries.map { "${settings.pathToAnimes}/${it.media.title.romaji}" }.toTypedArray()
                )
            )
        // Always exit process
        Runtime.getRuntime().addShutdownHook(thread(false) {
            process.destroy()
        })
        val reader = BufferedInputStream(process.inputStream)
        val watchStates = mutableMapOf<String, AnimeWatchState>()
        GlobalScope.launch {
            reader.bufferedReader().lines().forEach {
                val data = it.split(Regex(";"), 2)
                val fileName = data[1]
                val time = data[0].toInt()
                val watchState = watchStates.getOrPut(fileName) { AnimeWatchState(time) }
                watchState.add(time)
                if (watchState.done) {
                    watchStates.remove(fileName)
                    anilist.updateAnime(AnimeFile.fromFileName(fileName)!!)
                }
            }
        }
        println("AnimeWatch Started")
    }
}

class AnimeWatchState(var lastTime: Int) {
    private var duration: Int = 0
    val done get() = duration > 300

    fun add(time: Int) {
        val timeDiff = time - lastTime
        if (timeDiff <= 5) {
            duration += timeDiff
        }
        lastTime = time
    }
}