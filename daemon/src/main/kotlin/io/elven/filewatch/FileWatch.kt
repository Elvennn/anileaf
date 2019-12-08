package io.elven.filewatch

import io.elven.anilist.AniEntry
import io.elven.anilist.Anilist
import io.elven.anitomy.AnimeFile
import io.elven.download.DownloaderSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import kotlin.concurrent.thread

class FileWatch(private val anilist: Anilist, private val settings: DownloaderSettings) {
    private val animeWatchExec = "./animewatch" // TODO to settings
    private var job: Job? = null
    private val watchStates = mutableMapOf<String, AnimeWatchState>()

    fun start(aniEntries: Array<AniEntry>) {
        job?.cancel()
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
        job = GlobalScope.launch {
            reader.bufferedReader().lines().forEach {
                val data = it.split(Regex(";"), 2)
                val fileName = data[1]
                val time = data[0].toInt()
                val watchState = watchStates.getOrPut(fileName) { AnimeWatchState(time) }
                watchState.add(time)
                if (watchState.done) {
                    watchStates.remove(fileName)
                    anilist.updateAnime(AnimeFile.fromFileName(fileName))
                }
            }
        }
    }
}

class AnimeWatchState(private var lastTime: Int) {
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