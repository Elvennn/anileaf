package io.elven

import io.elven.anilist.AniEntry
import io.elven.anilist.Anilist
import io.elven.anitomy.AnimeFile
import io.elven.settings.AnileafInternalData
import io.elven.settings.AnileafSettings
import io.elven.torrent.TorrentEntry
import io.elven.torrent.TorrentFeed
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.simpleframework.xml.core.Persister
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class Daemon {
    private val anileafSettings = AnileafSettings()
    private val anileafData = AnileafInternalData()
    private val anilist = Anilist(anileafSettings, anileafData)

    init {
        //    fixedRateTimer(period = anileafSettings.settings.syncFrequency.toLong() * 1000) {
        //        syncTask()
        //    }
        //
        //    fixedRateTimer(period = 1000) {
        //        detectionTask()
        //    }
    }

    fun syncAnilistAndTorrents() {
        // TODO check for torrents & download
        // TODO sync plan to watch list

        try {
            anilist.sync()
            val currentList = anileafData.data.animeList
            val animeTorrentToDL = animeTorrentToDownload(currentList)
            if (!animeTorrentToDL.isEmpty()) {
                startTransmission()
            }
            animeTorrentToDL.forEach { (anime, torrent) ->
                val dowloadedAnimeState =
                    anileafData.animeDownloadState.getOrPut(anime.media.id) { mutableSetOf() }
                if (!dowloadedAnimeState.contains(torrent.animeFile!!.episode)) {
                    val cmd = arrayOf("sh", "-c", "transmission-remote -ne -a ${torrent.link} -w '${anileafSettings.settings.pathToAnimes}/${anime.media.title.romaji}/'")
                    Runtime.getRuntime().exec(cmd, null, null)
                    // TODO send notification
                    dowloadedAnimeState.add(torrent.animeFile!!.episode)
                }
            }
            println(animeTorrentToDL)
            anileafData.save()
        } catch (e: Exception) {
            // TODO Error when unable to get
            e.printStackTrace()
            exitProcess(1)
        }
    }

    fun startTransmission() {
        val process = ProcessBuilder("lsof", "-i:9091").start()
        process.waitFor(10, TimeUnit.SECONDS)
        if (process.exitValue() != 0) {
            ProcessBuilder("transmission-daemon").start()
        }
    }

    fun animeTorrentToDownload(currentList: Array<AniEntry>): List<Pair<AniEntry, TorrentEntry>> {
        val serializer = Persister()
        val feed = serializer.read(TorrentFeed::class.java, URL(anileafSettings.settings.torrentRSSFeed).readText(), false)
        feed.torrents.forEach { it.computeAnimeFile() }
        return feed.torrents
            // filter for watching animes
            .mapNotNull { entry ->
                val matchedAnime =
                    FuzzySearch.extractOne(entry.animeFile?.title ?: "", currentList.toList()) { it.media.title.romaji }
                // println("${entry.animeFile?.title}  ${matchedAnime.referent.media.title.romaji}    ${matchedAnime.score}")
                if (matchedAnime.score >= 90) {
                    Pair(matchedAnime.referent, entry)
                } else {
                    null
                }
            }
            // filter for needed episodes
            .filter { (anime, torrent) -> anime.progress < torrent.animeFile!!.episode }
            // filter for episode not downloded already
            .filter { (anime, torrent) ->
                val animeFolder = File("${anileafSettings.settings.pathToAnimes}/${anime.media.title.romaji}")
                if (animeFolder.exists()) {
                    animeFolder.listFiles()
                        ?.none { AnimeFile.fromFileName(it.name)?.episode == torrent.animeFile?.episode }
                        ?: true
                } else
                    true
            }
            // filter for video quality
            .filter { (_, torrent) ->
                anileafSettings.settings.minVideoQuality.any { it == torrent.animeFile?.quality ?: "" }
            }
            // filter for fansub settings
            .filter { (anime, torrent) ->
                val prefFansub =
                    anileafSettings.settings.animeSettings.find { it.aniID == anime.media.id }?.prefFasubTeam ?: ""
                prefFansub == "" || torrent.animeFile?.fansub == prefFansub
            }
            .toList()
        // TODO Download a unique release of the episode (check if already downloaded)
    }
}

fun main(args: Array<String>) {
    Daemon()
}

fun detectionTask() {
    // TODO
    println("bonjour")
}

fun <T> T.log(): T {
    println(this);
    return this
}