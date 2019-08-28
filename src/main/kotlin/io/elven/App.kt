package io.elven

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import io.elven.anilist.AniEntry
import io.elven.anilist.Anilist
import io.elven.utils.getResourceAsText
import java.util.EnumSet
import com.jayway.jsonpath.spi.mapper.MappingProvider
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import com.jayway.jsonpath.Option


fun main(args: Array<String>) {
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
    val token = getResourceAsText("token")
    val anilist = Anilist("Elvenn", token)
    anilist.updateAnime(21, 898)
    val animeListResponse = anilist.getAnimeList()

    val watchingList = JsonPath.parse(animeListResponse).read(
        """$..lists[?(@.status == 'CURRENT')].entries""",
        Array<Array<AniEntry>>::class.java
    )[0]

    println(watchingList.joinToString())

}