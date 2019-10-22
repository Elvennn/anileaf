package io.elven.anitomy

import com.dgtlrepublic.anitomyj.AnitomyJ
import com.dgtlrepublic.anitomyj.Element

data class AnimeFile(val title: String, val episode: Int = 0, val quality: String?, val fansub: String?) {
    companion object {
        fun fromAnitomy(anitomyElements: List<Element>): AnimeFile {
            val fileParts = anitomyElements.map { it.category to it.value }.toMap()
            return AnimeFile(
                fileParts[Element.ElementCategory.kElementAnimeTitle]
                    ?: error("Cannot get title from [${anitomyElements.joinToString { "${it.category}:${it.value}" }}]"),
                fileParts[Element.ElementCategory.kElementEpisodeNumber]?.toIntOrNull() ?: 0,
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
}