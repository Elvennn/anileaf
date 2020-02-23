package io.elven

import com.dgtlrepublic.anitomyj.AnitomyJ
import com.jayway.jsonpath.JsonPath
import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment
import io.elven.anilist.AniEntry
import io.elven.anilist.Anilist
import io.elven.anilist.AnilistApp
import io.elven.anitomy.AnimeFile
import io.elven.download.Downloader
import io.elven.download.DownloaderSettings
import io.elven.settings.AnileafInternalData
import io.elven.settings.DataFileHandler
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class CLI(private val args: Array<String>) {
    private val cliSettings = CLISettings.load()
    private val anileafData = AnileafInternalData()
    private val anilist = Anilist(cliSettings, anileafData)

    fun catchUp() {
        val downloaderSettings = DataFileHandler.load("settings.json", DownloaderSettings())
        val downloader = Downloader(downloaderSettings)
        val animeList = anilist.sync()
        val torrentFeeds = animeList.map {
            "${downloaderSettings.torrentRSSFeed}+${it.media.title.romaji.toLowerCase().replace(
                " ",
                "+"
            )}"
        }
        downloader.downloadMatchingTorrents(animeList, *torrentFeeds.toTypedArray(), exactness = 95)
    }

    fun init() {
        print("Anilist.co username : ")
        val userName = readLine()!!

        Desktop.getDesktop().browse(URI(AnilistApp.codeGenUrl))
        print("Use the opened link to generate code and paste it here : ")
        val code = readLine()!!

        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://anilist.co/api/v2/oauth/token"))
            .setHeader("Content-Type", "application/json")
            .setHeader("Accept", "application/json")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """{
                        "grant_type": "authorization_code",
                        "client_id": "${AnilistApp.clientId}",
                        "client_secret": "${AnilistApp.secret}",
                        "redirect_uri": "${AnilistApp.redirectUrl}",
                        "code": "$code"
                    }"""
                )
            )
            .build()

        val response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString()).body()
        val accessToken = JsonPath.read<String>(response, "$.access_token")
        cliSettings.anilistToken = accessToken
        cliSettings.anilistUserName = userName
        cliSettings.save()
    }

    fun list() {
        val table = AsciiTable()
        anilist.sync().forEach {
            table.addRule()
            val row = table.addRow(
                it.media.title.romaji,
                "${it.progress} / ${if (it.media.episodes != 0) it.media.episodes.toString() else "∞"}"
            )
            row.cells[1].context.textAlignment = TextAlignment.CENTER

        }
        table.addRule()
        println(table.render())
    }

    fun next() {
        anilist.sync().forEach {
            print("${it.media.title.romaji} (${it.progress}) : ")
            println(findNextEpisodes(it)
                ?.sortedBy { (animeFile, _) -> animeFile.episode }
                ?.joinToString(" ") { (animeFile, _) ->
                    animeFile.episode.toString()
                } ?: "")
        }
    }

    fun play() {
        val animeArg: String? = args.getOrNull(1)
        anilist.sync()
        val animeEntry = if (animeArg.isNullOrBlank()) {
            throw NotImplementedError()
        } else {
            parseAnimeArg(animeArg)
        }

        val animeEpisodeFile = findNextEpisode(animeEntry)
        val prettyFileName = "${animeEntry.media.title.romaji} episode ${animeEntry.progress + 1}"
        if (animeEpisodeFile == null) {
            print("No video files for $prettyFileName.")
            return
        }
        println("Playing $prettyFileName")
        Runtime.getRuntime().exec(arrayOf("sh", "-c", "vlc \"${animeEpisodeFile.absolutePath}\""))
    }

    fun sync() {
        anilist.sync()
    }

    fun update() {
        val animeArg: String = args.getOrNull(1) ?: throw NoSuchElementException("Missing anime title argument")
        val progress: Int =
            args.getOrNull(2)?.toInt() ?: throw NoSuchElementException("Missing anime progress argument")

        val parsedAnime = parseAnimeArg(animeArg)
        println("Updating ${parsedAnime.media.title.romaji} episode from ${parsedAnime.progress} to $progress ? (Y/n)")
        val answer = readLine()
        if (answer == null || answer == "" || answer.toLowerCase() == "y") {
            anilist.updateAnime(parsedAnime.media, progress)
            anileafData.saveWithUpdatedAnime(parsedAnime.withNewProgress(progress))
        }
    }

    private fun parseAnimeArg(animeArg: String): AniEntry {
        return anilist.sync().maxBy { it.media.title.partialMatch(animeArg) }
            ?: throw NoSuchElementException("Unable to find any currently watching anime for [$animeArg]")
    }

    private fun findNextEpisode(animeEntry: AniEntry): File? {
        return File("${cliSettings.pathToAnimes}/${animeEntry.media.title.romaji}")
            .listFiles()
            ?.firstOrNull { AnimeFile.fromFileName(it.name).episode == animeEntry.progress + 1 }
    }

    private fun findNextEpisodes(animeEntry: AniEntry): List<Pair<AnimeFile, File>>? {
        return File("${cliSettings.pathToAnimes}/${animeEntry.media.title.romaji}")
            .listFiles()
            ?.map { Pair(AnimeFile.fromFileName(it.name), it) }
            ?.filter { (animeFile, _) -> animeFile.episode > animeEntry.progress }
    }
}

fun main(args: Array<String>) {
    System.setProperty("org.slf4j.simpleLogger.logFile", "/tmp/truc")
    if (args.isEmpty()) {
        return
    }
    val cli = CLI(args)
    when (args[0]) {
        "catchup" -> cli.catchUp()
        "init" -> cli.init()
        "list" -> cli.list()
        "next" -> cli.next()
        "play" -> cli.play()
        "sync" -> cli.sync()
        "update" -> cli.update()
        else -> throw NoSuchElementException("Wrong cli mode [$args[0]")
    }
}

