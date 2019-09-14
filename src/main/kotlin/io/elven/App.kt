package io.elven

import io.elven.anilist.Anilist
import io.elven.torrent.TorrentFeed
import io.elven.utils.getResourceAsText
import org.simpleframework.xml.core.Persister
import java.net.URL
import com.dgtlrepublic.anitomyj.AnitomyJ
import io.elven.anitomy.AnimeFile
import io.elven.settings.AnileafInternalData
import io.elven.settings.AnileafSettings
import io.elven.torrent.TorrentEntry
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.io.File
import java.lang.Exception
import kotlin.concurrent.fixedRateTimer

fun main(args: Array<String>) {
    syncTask()
//    fixedRateTimer(period = AnileafSettings.settings.syncFrequency.toLong() * 1000) {
//        syncTask()
//    }
//
//    fixedRateTimer(period = 1000) {
//        detectionTask()
//    }
}

fun syncTask() {
    // TODO check for torrents & download
    // TODO maybe use the nextAiringEpisode in anilist media

    try {
        Anilist.sync()
        val currentList = AnileafInternalData.data.animeList

        val rssUrl = URL("https://nyaa.si/rss?q=vostfr")
        val serializer = Persister()
        val feed = serializer.read(TorrentFeed::class.java, rssUrl.readText(), false)
        feed.torrents.forEach { it.computeAnimeFile() }
        // filter for watching animes
        // filter for needed episodes
        // filter for episode not downloded already
        // filter for video quality & fansub settings
        // Download a unique release of the episode
        val animeToDL = feed.torrents.asSequence().mapNotNull { entry ->
            val matchedAnime =
                FuzzySearch.extractOne(entry.animeFile?.title ?: "", currentList.toList()) { it.media.title.romaji }
            // println("${entry.animeFile?.title}  ${matchedAnime.referent.media.title.romaji}    ${matchedAnime.score}")
            if (matchedAnime.score >= 90) {
                Pair(matchedAnime.referent, entry)
            } else {
                null
            }
        }
            .filter { (anime, torrent) -> anime.progress < torrent.animeFile?.episode ?: -1 }
            .filter { (anime, torrent) ->
                val animeFolder = File("${AnileafSettings.settings.pathToAnimes}/${anime.media.title.romaji}")
                if (animeFolder.exists()) {
                    animeFolder.listFiles()
                        ?.any { AnimeFile.fromFileName(it.name)?.episode == torrent.animeFile?.episode }
                        ?: false
                } else
                    false
            }
            .filter { (_, torrent) -> torrent.animeFile?.quality == AnileafSettings.settings.minVideoQuality }
            .filter { (anime, torrent) -> torrent.animeFile?.fansub == AnileafSettings.settings.animeSettings.find { it.aniID == anime.media.id }?.prefFasubTeam }
            .toList()
        println(animeToDL.joinToString())
    } catch (e: Exception) {
        // TODO Error when unable to get
        e.printStackTrace()
    }

}

fun detectionTask() {
    // TODO
    println("bonjour")
}