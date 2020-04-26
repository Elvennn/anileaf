package io.elven.filewatch

import io.elven.anilist.AniEntry
import io.elven.anilist.Anilist
import io.elven.anitomy.AnimeFile
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

class AnimeWatch(
    private val animeWatchExec: String,
    private val anilist: Anilist,
    private val pathToAnimes: String
) {
    private var job: Job? = null
    private val watchStates = mutableMapOf<String, AnimeWatchState>()

    fun start(aniEntries: Array<AniEntry>) {
        job?.cancel()
        val process =
            Runtime.getRuntime().exec(
                arrayOf(
                    animeWatchExec,
                    *aniEntries.map { "$pathToAnimes/${it.media.title.romaji}" }.toTypedArray()
                )
            )
        // Always exit process
        Runtime.getRuntime().addShutdownHook(thread(false) {
            process.destroy()
        })
        val reader = process.inputStream.buffered().bufferedReader()
        job = GlobalScope.launch(Dispatchers.IO) {
            while (this.isActive) {
                val line = reader.readLine()
                if (!this.isActive) {
                    return@launch
                }
                if (line == null) {
                    continue
                }
                val data = line.split(Regex(";"), 2)
                try {
                    val fileName = data[1]
                    val time = data[0].toInt()
                    if (fileName.endsWith(".part"))
                        continue
                    watchStates[fileName] ?: logger.info("Detecting $fileName")
                    val watchState = watchStates.getOrPut(fileName) { AnimeWatchState(time) }
                    watchState.add(time)
                    if (watchState.done) {
                        logger.info("$fileName watched. Updating anilist")
                        watchStates.remove(fileName)
                        anilist.updateAnime(AnimeFile.fromFileName(fileName))
                    }
                } catch (e: Exception) {
                    logger.error("Error: unable to handle anime watch data")
                }
            }
        }
    }

    companion object {
        private var logger: Logger = LoggerFactory.getLogger(AnimeWatch::class.java)
    }
}

class AnimeWatchState(private var lastTime: Int) {
    private var duration: Int = 0
    val done get() = duration > 300

    fun add(time: Int) {
        val timeDiff = time - lastTime
        if (timeDiff <= maxTimeToCount) {
            duration += timeDiff
        }
        lastTime = time
    }

    companion object {
        private const val maxTimeToCount = 20
    }
}