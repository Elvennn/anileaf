package io.elven

import io.elven.anilist.AniEntry
import io.elven.torrent.TorrentEntry

class Transmission(private val settings: DaemonSettings) {

    fun downloadAnime(anime: AniEntry, torrent: TorrentEntry) {
        val cmd = arrayOf(
            "sh",
            "-c",
            "transmission-remote ${settings.transmissionURL} -n ${settings.transmissionCredentials} -a ${torrent.link} -w '${settings.pathToAnimes}/${anime.media.title.romaji}/'"
        )
        Runtime.getRuntime().exec(cmd, null, null)
    }
}