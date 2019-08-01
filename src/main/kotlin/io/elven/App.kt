package io.elven

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import io.elven.anilist.Anilist
import io.elven.utils.getResourceAsText

fun main(args: Array<String>) {
    val token = getResourceAsText("token") ?: throw error(" NO TOKEN AVAILABLE")
    val anilist = Anilist("Elvenn", token)
    println(anilist.getAnimeGenres("Kiseijuu"))
    val animeList = anilist.getAnimeList()
    println(animeList)
    val conf = Configuration.defaultConfiguration()
    conf.mappingProvider(JacksonMappingProvider())
    JsonPath.using(conf)
    val watchingAnimes: List<String> = JsonPath.parse(animeList).read("\$..lists[?(@.status=='CURRENT')]..entries")
    println(watchingAnimes[0])
}