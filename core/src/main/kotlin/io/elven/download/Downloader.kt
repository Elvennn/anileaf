package io.elven.download

import io.elven.anilist.AniEntry
import io.elven.anitomy.AnimeFile
import io.elven.notification.FreeNotificationService
import org.simpleframework.xml.core.Persister
import java.io.File
import java.net.URL

class Downloader(private val settings: DownloaderSettings) {
    private val transmission = Transmission(settings)
    private val serializer = Persister()
    private val animeDownloadState: MutableMap<Int, MutableSet<Int>> = mutableMapOf()
    private val notificationService = FreeNotificationService(
        settings.freeSmsNotificationUser,
        settings.freeSmsNotificationPassword
    )

    fun downloadMatchingTorrents(
        animeList: Array<AniEntry>,
        vararg torrentFeedsUrl: String = arrayOf(settings.torrentRSSFeed),
        exactness: Int
    ) {
        torrentFeedsUrl
            .map { fetchTorrentFeed(it).torrents }
            .flatten()
            .toSet()
            .toTypedArray()
            .filterAndMapTorrentEntries(animeList, exactness)
            .download()
    }

    private fun fetchTorrentFeed(torrentFeedUrl: String): TorrentFeed =
        serializer.read(TorrentFeed::class.java, URL(torrentFeedUrl).readText(), false)

    private fun Array<TorrentEntry>.filterAndMapTorrentEntries(animeList: Array<AniEntry>, exactness: Int) =
        this.asSequence()
            .filterVideoQuality()
            .filterAndMapAnimeWithinList(animeList, exactness)
            .filterNeededEpisodes()
            .filterNotOwnedEpisodes()
            .filterFansubs()

    private fun Sequence<Pair<AniEntry, TorrentEntry>>.download() =
        this.forEach { (anime, torrent) ->
            val animeDownloadState = animeDownloadState.getOrPut(anime.media.id) { mutableSetOf() }
            if (!animeDownloadState.contains(torrent.animeFile!!.episode)) {
                transmission.downloadAnime(anime, torrent)
                animeDownloadState.add(torrent.animeFile!!.episode)
                notificationService.notifyAnime(torrent.animeFile!!)
                println(torrent.fileName)
            }
        }

    private fun Sequence<TorrentEntry>.filterAndMapAnimeWithinList(animeList: Array<AniEntry>, exactness: Int) =
        this.mapNotNull { entry ->
            val matchedAnime = animeList.firstOrNull { entry.animeFile?.strictMatchTitle(it.media, exactness) ?: false }
            if (matchedAnime != null) {
                Pair(matchedAnime, entry)
            } else {
                null
            }
        }

    private fun Sequence<Pair<AniEntry, TorrentEntry>>.filterNeededEpisodes() =
        this.filter { (anime, torrent) -> anime.progress < torrent.animeFile!!.episode }

    private fun Sequence<Pair<AniEntry, TorrentEntry>>.filterNotOwnedEpisodes() =
        this.filter { (anime, torrent) ->
            val animeFolder = File("${settings.pathToAnimes}/${anime.media.title.romaji}")
            if (animeFolder.exists()) {
                animeFolder.list()
                    ?.none {
                        try {
                            AnimeFile.fromFileName(it).episode == torrent.animeFile?.episode
                        } catch (e: Exception) {
                            false
                        }
                    }
                    ?: true
            } else
                true
        }

    private fun Sequence<TorrentEntry>.filterVideoQuality() =
        this.filter { torrent ->
            settings.minVideoQuality.any { it == torrent.animeFile?.quality ?: "" }
        }

    private fun Sequence<Pair<AniEntry, TorrentEntry>>.filterFansubs() =
        this.filter { (anime, torrent) ->
            val prefFansub =
                settings.animeSettings.find { it.aniID == anime.media.id }?.prefFasubTeam ?: ""
            prefFansub == "" || torrent.animeFile?.fansub == prefFansub
        }
}

fun <T> Sequence<T>.log(): Sequence<T> {
    println(this.joinToString("\n"))
    return this
}