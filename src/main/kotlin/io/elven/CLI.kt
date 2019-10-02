package io.elven

import com.dgtlrepublic.anitomyj.AnitomyJ
import com.jayway.jsonpath.JsonPath
import io.elven.anilist.AniEntry
import io.elven.anilist.Anilist
import io.elven.anilist.AnilistApp
import io.elven.anitomy.AnimeFile
import io.elven.settings.AnileafInternalData
import io.elven.settings.AnileafSettings
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

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
        val progress: Int =
            args.getOrNull(3)?.toInt() ?: throw NoSuchElementException("Missing anime progress argument")

        Anilist.updateAnime(parseAnimeArg(animeArg).media, progress)
    }

    fun sync() {
        Anilist.sync()
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
        AnileafSettings.settings.anilistToken = accessToken
        AnileafSettings.settings.anilistUserName = userName
        AnileafSettings.save()
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

