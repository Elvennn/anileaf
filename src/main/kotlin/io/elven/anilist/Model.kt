package io.elven.anilist

data class AniEntry(val progress: Int, val media: AniMedia)

data class AniMedia(val id: Int, val title: AniTitle) {
    override fun toString(): String {
        return "Media(id=$id, title=${title.romaji})"
    }
}

data class AniTitle(val romaji: String)