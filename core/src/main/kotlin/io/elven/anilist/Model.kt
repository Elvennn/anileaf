package io.elven.anilist

import me.xdrop.fuzzywuzzy.FuzzySearch
import kotlin.math.max

data class AniEntry(val progress: Int = -1, val media: AniMedia = AniMedia()) {
    fun withNewProgress(progress: Int): AniEntry {
        return AniEntry(progress, media)
    }
}

data class AniMedia(val id: Int = -1, val title: AniTitle = AniTitle(), val episodes: Int = -1) {
    override fun toString(): String {
        return "Media(id=$id, title=${title.romaji})"
    }
}

data class AniTitle(val romaji: String = "", val english: String = "") {
    fun match(title: String) = max(
        FuzzySearch.partialRatio(romaji.toLowerCase(), title.toLowerCase()),
        FuzzySearch.partialRatio(english.toLowerCase(), title.toLowerCase())
    )
}
