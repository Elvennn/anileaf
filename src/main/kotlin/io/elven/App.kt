package io.elven

import com.beust.klaxon.*
import io.elven.anilist.AniEntry
import io.elven.anilist.Anilist
import io.elven.utils.getResourceAsText

fun main(args: Array<String>) {
    val token = getResourceAsText("token") ?: throw error(" NO TOKEN AVAILABLE")
    val anilist = Anilist("Elvenn", token)
    println(anilist.getAnimeGenres("Kiseijuu"))
    val animeList = anilist.getAnimeList()
    println(animeList)

    val watchingList: List<AniEntry> = (Parser.default().parse(StringBuilder(animeList)) as JsonObject)
        .lookup<JsonArray<JsonObject>>("data.MediaListCollection.lists")[0]
        .filter { it.string("status") == "CURRENT" }[0]
        .array<JsonObject>("entries")!!
        .map { Klaxon().parseFromJsonObject<AniEntry>(it)!! }

    println(watchingList.joinToString { it.toString() })

}