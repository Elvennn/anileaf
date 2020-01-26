package io.elven.anilist

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object Graphql {
    private var logger: Logger = LoggerFactory.getLogger(Graphql::class.java)
    private val httpClient: HttpClient = HttpClient.newBuilder().build()

    fun query(url: String, query: GraphqlQuery, token: String?): String {
        return query(url, query.getQueryString(), query.getVariablesString(), token)
    }

    fun query(url: String, query: String, variables: String = "{ }", token: String? = null): String {
        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .setHeader("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("""{"query": "$query", "variables": $variables}"""))
        if (token != null) requestBuilder.setHeader("Authorization", "Bearer $token")
        val request = requestBuilder.build()
        logger.debug(request.toString())
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body()
    }
}