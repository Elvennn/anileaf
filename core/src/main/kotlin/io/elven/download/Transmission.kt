package io.elven.download

import io.elven.anilist.AniEntry

class Transmission(private val settings: DownloaderSettings) {

    fun downloadAnime(anime: AniEntry, torrent: TorrentEntry) {
        val cmd = arrayOf(
            "sh",
            "-c",
            "transmission-remote ${settings.transmissionURL} -n ${settings.transmissionCredentials} -a ${torrent.link} -w '${settings.pathToDownloadAnimes}/${anime.media.title.romaji}/'"
        )
        val returnCode = Runtime.getRuntime().exec(cmd, null, null).waitFor()
        if (returnCode != 0) {
            error("Cannot access TransmissionBT")
        }
    }
}