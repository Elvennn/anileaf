package io.elven

import io.elven.utils.getResourceAsText
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object Graphql {
    private val httpClient: HttpClient = HttpClient.newBuilder().build()

    fun getQuery(queryType: QueryType): String =
        getResourceAsText("graphqlQueries/${queryType.requestFile}.graphql")!!.replace("\n", "")

    fun query(url: String, queryType: QueryType, token: String?): String {
        val query = getQuery(queryType)
        return query(url, query, token)
    }

    fun query(url: String, query: String, token: String?): String {
        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .setHeader("Content-Type", "application/json")
        requestBuilder.POST(HttpRequest.BodyPublishers.ofString("""{"query": "$query", "variables": { "userName": "Elvenn"}}"""))

        if (token != null) requestBuilder.setHeader("Authorization", "Bearer $token")
        val request = requestBuilder.build()
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body()
    }
}