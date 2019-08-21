package io.elven.anilist

import io.elven.utils.getResourceAsText

class GraphqlQuery(val queryType: QueryType, private val variables: Array<Pair<String, Any>>) {

    fun getQueryString(): String =
        getResourceAsText("graphqlQueries/${queryType.requestFile}.graphql").replace("\n", "")

    fun getVariablesString(): String = variables.joinToString(", ", "{", "}") { """"${it.first}": "${it.second}"""" }
}