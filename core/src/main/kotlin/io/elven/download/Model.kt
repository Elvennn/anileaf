package io.elven.download

import io.elven.anitomy.AnimeFile
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class TorrentFeed(
    @field:Path(value = "channel")
    @field:ElementList(
        entry = "item",
        inline = true
    ) var torrents: MutableList<TorrentEntry> = mutableListOf()
)

@Root
data class TorrentEntry(
    @field:Element var link: String = "",
    @field:Element(name = "title") var fileName: String = ""
) {
    val animeFile: AnimeFile? by lazy { AnimeFile.fromFileName(fileName) }
}