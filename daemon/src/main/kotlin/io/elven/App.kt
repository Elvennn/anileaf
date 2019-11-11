package io.elven

import io.elven.anilist.AniEntry
import io.elven.anilist.Anilist
import io.elven.anitomy.AnimeFile
import io.elven.settings.AnileafInternalData
import io.elven.settings.DataFileHandler
import io.elven.torrent.TorrentEntry
import io.elven.torrent.TorrentFeed
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.simpleframework.xml.core.Persister
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer
import kotlin.system.exitProcess

class Daemon(basePath: String? = null) {
    private val settings = DataFileHandler.load("settings.json", DaemonSettings(), basePath)
    private val anileafData = AnileafInternalData(basePath)
    private val anilist = Anilist(settings, anileafData)
    private val transmission = Transmission(settings)

    init {
//        fixedRateTimer(period = settings.syncFrequency.toLong() * 1000) {
            syncAnilistAndTorrents()
//        }
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
            animeTorrentToDL.forEach { (anime, torrent) ->
                val animeDownloadState =
                    anileafData.data.animeDownloadState.getOrPut(anime.media.id) { mutableSetOf() }
                if (!animeDownloadState.contains(torrent.animeFile!!.episode)) {
                    transmission.downloadAnime(anime, torrent)
                    // TODO send notification
                    animeDownloadState.add(torrent.animeFile!!.episode)
                    println(torrent)
                }
            }
//            println(animeTorrentToDL.joinToString("\n"))
            anileafData.save()
        } catch (e: Exception) {
            // TODO Error when unable to get
            e.printStackTrace()
            exitProcess(1)
        }
    }

    fun animeTorrentToDownload(currentList: Array<AniEntry>): List<Pair<AniEntry, TorrentEntry>> {
        val serializer = Persister()
        val feed = serializer.read(TorrentFeed::class.java, URL(settings.torrentRSSFeed).readText(), false)
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
                val animeFolder = File("${settings.pathToAnimes}/${anime.media.title.romaji}")
                if (animeFolder.exists()) {
                    animeFolder.list()
                        ?.none {
                            try {
                                AnimeFile.fromFileName(it)?.episode == torrent.animeFile?.episode
                            } catch (e: Exception) {
                                false
                            }
                        }
                        ?: true
                } else
                    true
            }
            // filter for video quality
            .filter { (_, torrent) ->
                settings.minVideoQuality.any { it == torrent.animeFile?.quality ?: "" }
            }
            // filter for fansub settings
            .filter { (anime, torrent) ->
                val prefFansub =
                    settings.animeSettings.find { it.aniID == anime.media.id }?.prefFasubTeam ?: ""
                prefFansub == "" || torrent.animeFile?.fansub == prefFansub
            }
            .toList()
    }
}

fun main(args: Array<String>) {
    val basePath: String? = args.getOrNull(0)
    Daemon(basePath)
}
