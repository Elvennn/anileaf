package io.elven


class Anilist(private val userName: String, private val token: String) {
    private val ANILIST_API_URL = "https://graphql.anilist.co/"

    fun get(queryType: QueryType): String {
        return Graphql.query(ANILIST_API_URL, queryType, token)
    }


    fun getAnimeGenres(anime: String): String {
        val query = """{ Media(search: \"$anime\") {genres,title {romaji}}}"""
        return Graphql.query(ANILIST_API_URL, query, token)
    }
}