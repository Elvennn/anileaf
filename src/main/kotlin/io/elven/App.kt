package io.elven

import io.elven.anilist.Anilist
import io.elven.utils.getResourceAsText


fun main(args: Array<String>) {

    val token = getResourceAsText("token")
    val anilist = Anilist("Elvenn", token)
    val currentList = anilist.getAnimeCurrentList()

//    anilist.updateAnime(currentList[0].media, 898)

    println(currentList.joinToString())

}