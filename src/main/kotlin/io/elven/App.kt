package io.elven

import io.elven.anilist.Anilist
import io.elven.torrent.TorrentFeed
import io.elven.utils.getResourceAsText
import org.simpleframework.xml.core.Persister
import java.net.URL
import com.dgtlrepublic.anitomyj.AnitomyJ
import io.elven.anitomy.AnimeFile
import io.elven.settings.AnileafSettings

fun animelistfetch() {
    val token = getResourceAsText("token")
    val currentList = Anilist.getAnimeCurrentList()

//    anilist.updateAnime(currentList[0].media, 898)

    println(currentList.joinToString())
}

fun main(args: Array<String>) {
    val currentList = Anilist.getAnimeCurrentList()
    val rssUrl = URL("https://nyaa.si/rss?q=vostfr")
    val serializer = Persister()

    val feed = serializer.read(TorrentFeed::class.java, rssUrl.readText(), false)

    val torrentAnimeFiles = feed.torrents.map { AnimeFile.fromAnitomy(AnitomyJ.parse(it.title)) }
    val animeToDL = currentList.filter { animeListEntry -> torrentAnimeFiles.filter { it.title?.contains(animeListEntry.media.title.romaji) ?: false }.any()}
    println(animeToDL.joinToString { it.media.title.romaji })
}