package io.elven.anilist


class Anilist(private val userName: String, private val token: String) {
    private val ANILIST_API_URL = "https://graphql.anilist.co/"

    fun get(query: GraphqlQuery): String {
//        println("${query.queryType} ${query.getQueryString()}")
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

    fun updateAnime(mediaId: Int, prgoress: Int) {
        get(
            GraphqlQuery(
                QueryType.UPDATE_LIST,
                arrayOf(Pair("mediaId", mediaId), Pair("progress", prgoress))
            )
        )
    }
}