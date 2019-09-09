package io.elven

import io.elven.anilist.AniEntry

class CLI {
    private val args: Array<String>
    private val animeList: Array<AniEntry>

    constructor(args: Array<String>) {
        this.args = args
        // TODO get current list from disk
        animeList = emptyArray()
    }


    fun play() {
        val animeArg: String? = args.getOrNull(2)
        val animeEntry = if (animeArg.isNullOrBlank()) {
            // TODO anime choice in list
            throw NotImplementedError()
        } else {
            parseAnimeArg(animeArg)
        }
        val progress = animeEntry.progress
        // TODO look for anime file in path & open it

    }

    fun list() {
        println(
            animeList.joinToString("\n") {
                "${it.media.title.romaji}\t\t\t${it.progress}\t/\t?"
                // TODO replace with total episode count
            }
        )
    }

    fun update() {
        val animeArg: String = args.getOrNull(2) ?: throw NoSuchElementException("Missing anime title argument")
        val progress: Int = args.getOrNull(3)?.toInt()  ?: throw NoSuchElementException("Missing anime progress argument")

        // TODO have an initialized anilist
        // anilist.updateAnime(parseAnimeArg(animeArg), progress)
    }

    private fun parseAnimeArg(animeArg: String): AniEntry {
        return animeList.minBy { it.media.title.romaji.contains(animeArg, true) } ?: throw NoSuchElementException("Unable to find any currently watching anime for [$animeArg]")
    }
}

fun main(args: Array<String>) {
    println(args.joinToString()) // TODO check if first or second in args array

    if (args.isEmpty()) {
        return
    }
    val cli = CLI(args)
    when (args[1]) {
        "list" -> cli.list()
        "update" -> cli.update()
        "play" -> cli.play()
        else -> throw NoSuchElementException("Wrong cli mode [$args[1]")
    }
}

