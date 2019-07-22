package io.elven

import io.elven.anilist.Anilist
import io.elven.utils.getResourceAsText

fun main(args: Array<String>) {
    val token = getResourceAsText("token")!!
    val anilist = Anilist("Elvenn", token)
    println(anilist.getAnimeGenres("Kiseijuu"))
    println(anilist.getAnimeList())
}