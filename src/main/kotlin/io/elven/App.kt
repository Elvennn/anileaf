package io.elven

import com.beust.klaxon.*
import io.elven.anilist.AniEntry
import io.elven.anilist.Anilist
import io.elven.utils.getResourceAsText

fun main(args: Array<String>) {
    val token = getResourceAsText("token")
    val anilist = Anilist("Elvenn", token)
    anilist.updateAnime(21, 898)
    val animeListResponse = anilist.getAnimeList()
    val parser = Parser.default()
    val watchingList: List<AniEntry> = (parser.parse(StringBuilder(animeListResponse)) as JsonObject)
        .lookup<JsonArray<JsonObject>>("data.MediaListCollection.lists")[0]
        .filter { it.string("status") == "CURRENT" }[0]
        .array<JsonObject>("entries")!!
        .map { Klaxon().parseFromJsonObject<AniEntry>(it)!! }

    println(watchingList.joinToString { it.toString() })

}