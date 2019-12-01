package io.elven.anitomy

import com.dgtlrepublic.anitomyj.AnitomyJ
import com.dgtlrepublic.anitomyj.Element
import io.elven.anilist.AniMedia

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
                throw RuntimeException("Cannot parse anime file: $filename")
    }

    fun strictMatchTitle(anime: AniMedia): Boolean {
        val rawRatio = anime.title.match(title)
        if (season != 1 && rawRatio >= 50) {
            return titlesWithSeason().map { anime.title.match(it) }.max() == 100
        }
        return rawRatio >= 94
    }

    fun looseMatchTitle(anime: AniMedia): Boolean {
        val rawRatio = anime.title.match(title)
        return rawRatio >= 50
    }

    private fun titlesWithSeason() = setOf(
        "$title $season",
        "$title $seasonPrefix$season",
        "$title $seasonPrefix $season",
        "$title ${seasonPrefix}0$season"
    )
}