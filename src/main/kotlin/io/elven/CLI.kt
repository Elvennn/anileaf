package io.elven

import com.dgtlrepublic.anitomyj.AnitomyJ
import io.elven.anilist.AniEntry
import io.elven.anilist.Anilist
import io.elven.anitomy.AnimeFile
import io.elven.settings.AnileafInternalData
import io.elven.settings.AnileafSettings
import java.awt.Desktop
import java.io.File
import java.nio.file.Files

class CLI(private val args: Array<String>, private val animeList: Array<AniEntry>) {

    // TODO test this
    fun play() {
        val animeArg: String? = args.getOrNull(1)
        val animeEntry = if (animeArg.isNullOrBlank()) {
            // TODO anime choice in list
            throw NotImplementedError()
        } else {
            parseAnimeArg(animeArg)
        }
        val progress = animeEntry.progress
        val animeFile = File("${AnileafSettings.settings.pathToAnimes}/${animeEntry.media.title.romaji}")
            .listFiles()
            ?.first { AnimeFile.fromAnitomy(AnitomyJ.parse(it.name)).episode == animeEntry.progress }
        if (animeFile == null) {
            print("No video files for ${animeEntry.media.title.romaji} episode ${animeEntry.progress}.")
            return
        }
        Desktop.getDesktop().open(animeFile)
    }

    // TODO Test this
    fun list() {
        println(
            animeList.joinToString("\n") {
                "${it.media.title.romaji}\t\t\t${it.progress}\t/\t${it.media.episodes}"
            }
        )
    }

    // TODO Test this
    fun update() {
        val animeArg: String = args.getOrNull(2) ?: throw NoSuchElementException("Missing anime title argument")
        val progress: Int = args.getOrNull(3)?.toInt()  ?: throw NoSuchElementException("Missing anime progress argument")

        Anilist.updateAnime(parseAnimeArg(animeArg).media, progress)
    }

    fun sync() {
        AnileafInternalData.data.animeList = Anilist.getAnimeCurrentList()
        AnileafInternalData.save()
    }

    fun init() {
        File(AnileafSettings.path).mkdir()
    }

    private fun parseAnimeArg(animeArg: String): AniEntry {
        return animeList.minBy { it.media.title.romaji.contains(animeArg, true) } ?: throw NoSuchElementException("Unable to find any currently watching anime for [$animeArg]")
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        return
    }
    val cli = CLI(args, AnileafInternalData.data.animeList)
    when (args[0]) {
        "list" -> cli.list()
        "update" -> cli.update()
        "play" -> cli.play()
        "init" -> cli.init()
        "sync" -> cli.sync()
        else -> throw NoSuchElementException("Wrong cli mode [$args[0]")
    }
}

