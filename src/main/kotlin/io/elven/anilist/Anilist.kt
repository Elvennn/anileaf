package io.elven.anilist

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import org.slf4j.LoggerFactory
import java.util.*


class Anilist(private val userName: String, private val token: String) {
    private val ANILIST_API_URL = "https://graphql.anilist.co/"

    fun get(query: GraphqlQuery): String {
        logger.debug("${query.queryType} ${query.getQueryString()}")
        return Graphql.query(ANILIST_API_URL, query, token)
    }

    fun getAnimeCurrentList(): Array<AniEntry> {
        val response = get(
            GraphqlQuery(
                QueryType.ANIME_LIST,
                arrayOf(Pair("userName", userName))
            )
        )
        return JsonPath.parse(response).read(
            """$..lists[?(@.status == 'CURRENT')].entries""",
            Array<Array<AniEntry>>::class.java
        )[0]
    }

    fun updateAnime(media: AniMedia, prgoress: Int) {
        get(
            GraphqlQuery(
                QueryType.UPDATE_LIST,
                arrayOf(Pair("mediaId", media.id), Pair("progress", prgoress))
            )
        )
    }


    companion object {
        private val logger = LoggerFactory.getLogger(Anilist::class.java)
        init {
            Configuration.setDefaults(object : Configuration.Defaults {

                private val jsonProvider = JacksonJsonProvider()
                private val mappingProvider = JacksonMappingProvider()

                override fun jsonProvider(): JsonProvider {
                    return jsonProvider
                }


                override fun mappingProvider(): MappingProvider {
                    return mappingProvider
                }

                override fun options(): Set<Option> {
                    return EnumSet.noneOf(Option::class.java)
                }
            })
        }
    }
}