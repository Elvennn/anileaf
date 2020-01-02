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
        fun fromFileName(filename: String): AnimeFile {
            if (filename.trim() == "")
                throw RuntimeException("Cannot parse anime file: $filename")

            val fileParts = AnitomyJ.parse(filename).map { it.category to it.value }.toMap()
            return AnimeFile(
                fileParts[Element.ElementCategory.kElementAnimeTitle]
                    ?: error("Cannot get title from $filename"),
                fileParts[Element.ElementCategory.kElementEpisodeNumber]?.toIntOrNull() ?: 0,
                fileParts[Element.ElementCategory.kElementAnimeSeason]?.toIntOrNull() ?: 1,
                fileParts[Element.ElementCategory.kElementAnimeSeasonPrefix] ?: "",
                fileParts[Element.ElementCategory.kElementVideoResolution],
                fileParts[Element.ElementCategory.kElementReleaseGroup]
            )
        }

        val titleSanitizer = "(S?\\d{0,2})\$".toRegex()
        val seasonExtractor = "[\\d]*\$".toRegex()
    }

    fun strictMatchTitle(anime: AniMedia, cutoff: Int): Boolean {
        val rawRatio = anime.title.match(title)
        if (rawRatio >= 50 && anime.title.estimatedSeason != null) {
            if (season != 1 && season != anime.title.estimatedSeason) {
                return false
            }
            return possibleTitles().map { anime.title.match(it) }.max() == 100
        }
        return rawRatio >= cutoff
    }

    fun looseMatchTitle(anime: AniMedia): Boolean {
        val rawRatio = anime.title.match(title)
        return rawRatio >= 50
    }

    private fun possibleTitles(): Set<String> {
        val sanitizedTitle = titleSanitizer.replace(title, "").trim()
        return arrayOf(season, seasonExtractor.find(title)?.value).flatMap {
            setOf(
                "$sanitizedTitle $it",
                "$sanitizedTitle S$it",
                "$sanitizedTitle S0$it"
            )
        }.toSet()
    }
}