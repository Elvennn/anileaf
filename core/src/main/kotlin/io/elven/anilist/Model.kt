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
    private val romajiWithoutSeason by lazy { getWithoutSeason(romaji) }
    private val englishWithoutSeason by lazy { getWithoutSeason(english) }
    val estimatedSeason by lazy { seasonRegex.find(romaji)?.value?.toIntOrNull() }

    private fun getWithoutSeason(str: String) = str.replace(seasonRegex, "").trim()

    fun partialMatch(title: String) = max(
        FuzzySearch.partialRatio(romaji.toLowerCase(), title.toLowerCase()),
        FuzzySearch.partialRatio(english.toLowerCase(), title.toLowerCase())
    )

    fun match(title: String) = max(
        FuzzySearch.ratio(romaji.toLowerCase(), title.toLowerCase()),
        FuzzySearch.ratio(english.toLowerCase(), title.toLowerCase())
    )

    fun matchWithoutSeason(title: String): Int {
        val titleWithoutSeason = getWithoutSeason(title)
        return max(
            FuzzySearch.ratio(romajiWithoutSeason.toLowerCase(), titleWithoutSeason.toLowerCase()),
            FuzzySearch.ratio(englishWithoutSeason.toLowerCase(), titleWithoutSeason.toLowerCase())
        )
    }

    companion object {
        private val seasonRegex = "[0-9]+$".toRegex()
    }
}
