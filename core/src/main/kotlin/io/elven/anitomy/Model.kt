package io.elven.anitomy

import com.dgtlrepublic.anitomyj.AnitomyJ
import com.dgtlrepublic.anitomyj.Element
import io.elven.anilist.AniEntry

data class AnimeFile(
    val title: String,
    val episode: Int,
    val season: Int,
    val seasonPrefix: String,
    val quality: String?,
    val fansub: String?
) {
    companion object {
        fun fromAnitomy(anitomyElements: List<Element>): AnimeFile {
            val fileParts = anitomyElements.map { it.category to it.value }.toMap()
            return AnimeFile(
                fileParts[Element.ElementCategory.kElementAnimeTitle]
                    ?: error("Cannot get title from [${anitomyElements.joinToString { "${it.category}:${it.value}" }}]"),
                fileParts[Element.ElementCategory.kElementEpisodeNumber]?.toIntOrNull() ?: 0,
                fileParts[Element.ElementCategory.kElementAnimeSeason]?.toIntOrNull() ?: 1,
                fileParts[Element.ElementCategory.kElementAnimeSeasonPrefix] ?: "",
                fileParts[Element.ElementCategory.kElementVideoResolution],
                fileParts[Element.ElementCategory.kElementReleaseGroup]
            )
        }

        fun fromFileName(filename: String) =
            if (filename.trim() != "")
                fromAnitomy(AnitomyJ.parse(filename))
            else
                null
    }

    fun maxRatioWith(anime: AniEntry): Boolean {
        val rawRatio = anime.media.title.match(title)
        if (season != 1 && rawRatio >= 50) {
            return titlesWithSeason().map { anime.media.title.match(it) }.max() == 100
        }
        return rawRatio > 95
    }

    private fun titlesWithSeason() = setOf(
        "$title $season",
        "$title $seasonPrefix$season",
        "$title $seasonPrefix $season",
        "$title ${seasonPrefix}0$season"
    )
}