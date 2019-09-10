package io.elven.anilist

data class AniEntry(val progress: Int = -1, val media: AniMedia = AniMedia())

data class AniMedia(val id: Int = -1, val title: AniTitle = AniTitle()) {
    override fun toString(): String {
        return "Media(id=$id, title=${title.romaji})"
    }
}

data class AniTitle(val romaji: String = "", val english: String = "")
