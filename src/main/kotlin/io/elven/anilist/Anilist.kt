package io.elven.anilist


class Anilist(private val userName: String, private val token: String) {
    private val ANILIST_API_URL = "https://graphql.anilist.co/"

    fun get(query: GraphqlQuery): String {
        return Graphql.query(ANILIST_API_URL, query, token)
    }

    fun getAnimeList(): String {
        return get(
            GraphqlQuery(
                QueryType.ANIME_LIST,
                arrayOf(Pair("userName", userName))
            )
        )
    }

    fun getAnimeGenres(anime: String): String {
        val query = """{ Media(search: \"$anime\") {genres,title {romaji}}}"""
        return Graphql.query(ANILIST_API_URL, query)
    }
}