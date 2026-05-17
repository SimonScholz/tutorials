package dev.simonscholz

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import io.github.cdimascio.dotenv.dotenv

suspend fun main() {
    val env = dotenv()
    val token = env["TOKEN"]
    val serverUrl = env["SERVER_URL"]
    val sku = env["SKU"]
    val facility = env["FACILITY"]

    val apolloClient =
        ApolloClient
            .Builder()
            .serverUrl(serverUrl)
            .addHttpHeader("Authorization", "Bearer $token")
            .build()

    val response =
        apolloClient
            .query(FetchStocksQuery(sku, facility))
            .execute()

    if (response.errors.isNullOrEmpty()) {
        println("StocksQuery.totalCount=${response.data?.stocksV3?.totalCount}")
        println("StocksQuery.edges=${response.data?.stocksV3?.edges}")

        // deleteStock(response, apolloClient)
    } else {
        println("Errors: ${response.errors}")
    }
}

private suspend fun deleteStock(
    response: ApolloResponse<FetchStocksQuery.Data>,
    apolloClient: ApolloClient,
) {
    response.data?.stocksV3?.edges?.forEach { edge ->
        edge?.node?.id?.let {
            val deleteResponse =
                apolloClient
                    .mutation(DeleteStockMutation(it))
                    .execute()
            if (deleteResponse.errors.isNullOrEmpty()) {
                println("DeleteData=${deleteResponse.data}")
            } else {
                println("DeleteData=${deleteResponse.errors}")
            }
        }
    }
}
