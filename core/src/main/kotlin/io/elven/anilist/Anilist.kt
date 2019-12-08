package io.elven.anilist

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import io.elven.anitomy.AnimeFile
import io.elven.settings.AnileafInternalData
import io.elven.settings.GlobalSettings
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*


class Anilist(settings: GlobalSettings, private val anileafData: AnileafInternalData) {

    companion object {
        private const val ANILIST_API_URL = "https://graphql.anilist.co/"
        private var logger: Logger = LoggerFactory.getLogger(Anilist::class.java)
    }

    private val userName: String = settings.anilistUserName
    private val token: String = settings.anilistToken

    fun sync(): Array<AniEntry> {
        anileafData.data.animeList = try {
            getAnimeCurrentList()
        } catch (exception: Exception) {
            logger.error("UNABLE TO SYNC")
            println("ENABLE TO SYNC")
            emptyArray()
        }
        anileafData.save()
        return anileafData.data.animeList
    }

    private fun get(query: GraphqlQuery): String {
        logger.info("${query.queryType} ${query.getQueryString()}")
        val response = Graphql.query(ANILIST_API_URL, query, token)
        logger.info(response)
        return response
    }

    private fun getAnimeCurrentList(): Array<AniEntry> {
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

    fun updateAnime(media: AniMedia, progress: Int) {
        get(
            GraphqlQuery(
                QueryType.UPDATE_LIST,
                arrayOf(Pair("mediaId", media.id), Pair("progress", progress))
            )
        )
    }

    fun updateAnime(animeFile: AnimeFile): Boolean {
        val animes = sync()
        val aniEntry = animes.firstOrNull { animeFile.looseMatchTitle(it.media) }
        if (aniEntry != null && animeFile.episode > aniEntry.progress) {
            updateAnime(aniEntry.media, animeFile.episode)
            println("${aniEntry.media.title.romaji} updated to episode ${animeFile.episode}")
            return true
        }
        return false
    }

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