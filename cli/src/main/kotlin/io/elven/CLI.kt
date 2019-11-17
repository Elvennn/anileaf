package io.elven

import com.dgtlrepublic.anitomyj.AnitomyJ
import com.jayway.jsonpath.JsonPath
import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment
import io.elven.anilist.AniEntry
import io.elven.anilist.Anilist
import io.elven.anilist.AnilistApp
import io.elven.anitomy.AnimeFile
import io.elven.settings.AnileafInternalData
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
    private val animeList = anileafData.data.animeList

    fun play() {
        val animeArg: String? = args.getOrNull(1)
        anilist.sync()
        val animeEntry = if (animeArg.isNullOrBlank()) {
            // TODO anime choice in list
            throw NotImplementedError()
        } else {
            parseAnimeArg(animeArg)
        }

        val animeFile = File("${cliSettings.pathToAnimesCLI}/${animeEntry.media.title.romaji}")
            .listFiles()
            ?.firstOrNull { AnimeFile.fromAnitomy(AnitomyJ.parse(it.name)).episode == animeEntry.progress + 1 }
        if (animeFile == null) {
            print("No video files for ${animeEntry.media.title.romaji} episode ${animeEntry.progress + 1}.")
            return
        }
        Desktop.getDesktop().open(animeFile)
    }

    fun list() {
        val table = AsciiTable()
        animeList.forEach {
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

    fun update() {
        val animeArg: String = args.getOrNull(1) ?: throw NoSuchElementException("Missing anime title argument")
        val progress: Int =
            args.getOrNull(2)?.toInt() ?: throw NoSuchElementException("Missing anime progress argument")

        val parsedAnime = parseAnimeArg(animeArg)
        anilist.updateAnime(parsedAnime.media, progress)
        anileafData.saveWithUpdatedAnime(parsedAnime.withNewProgress(progress))
    }

    fun sync() {
        anilist.sync()
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

    private fun parseAnimeArg(animeArg: String): AniEntry {
        return animeList.maxBy { it.media.title.match(animeArg) }
            ?: throw NoSuchElementException("Unable to find any currently watching anime for [$animeArg]")
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        return
    }
    val cli = CLI(args)
    when (args[0]) {
        "list" -> cli.list()
        "update" -> cli.update()
        "play" -> cli.play()
        "init" -> cli.init()
        "sync" -> cli.sync()
        else -> throw NoSuchElementException("Wrong cli mode [$args[0]")
    }
}

