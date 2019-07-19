import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {
    println("Hello World!")
    val client = HttpClient.newBuilder().build();
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://graphql.anilist.co/"))
        .setHeader("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString("{\"query\": \"{ Media(search: \\\"One Piece\\\") {genres,title {romaji}}}\", \"variables\": {}}"))
        .build()
    println(client.send(request, HttpResponse.BodyHandlers.ofString()).body())

}