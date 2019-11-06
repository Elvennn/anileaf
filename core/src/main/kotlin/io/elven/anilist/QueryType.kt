package io.elven.anilist

enum class QueryType(val requestFile: String) {
    ANIME_LIST("animeList"),
    UPDATE_LIST("updateList"),
}