package io.elven.utils

import java.lang.Exception
import java.net.URL

private object Utils {
    fun getResourceURL(path: String): URL? {
        return javaClass.classLoader.getResource(path)
    }
}
fun getResourceAsText(path: String): String {
    return Utils.getResourceURL(path)?.readText() ?: throw Exception("Cannot get $path")
}