package io.elven.anilist

data class AniEntry(val id: Long, val media: AniMedia) {
    override fun toString(): String {
        return "AniEntry(id=$id, title=${media.title.romaji})"
    }
}

data class AniMedia(val title: AniTitle)

data class AniTitle(val romaji: String)