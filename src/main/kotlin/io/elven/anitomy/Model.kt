package io.elven.anitomy

import com.dgtlrepublic.anitomyj.Element

data class AnimeFile(val title: String? = "", val episode: Int? = -1, val quality: String? = null) {
    companion object {
        fun fromAnitomy(anitomyElements: List<Element>): AnimeFile {
            val fileParts = anitomyElements.map { it.category to it.value }.toMap()
            return AnimeFile(
                fileParts[Element.ElementCategory.kElementAnimeTitle],
                fileParts[Element.ElementCategory.kElementEpisodeNumber]?.toInt(),
                fileParts[Element.ElementCategory.kElementVideoResolution]
            )
        }
    }
}