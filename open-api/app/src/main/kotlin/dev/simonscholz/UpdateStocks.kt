package dev.simonscholz

import dev.simonscholz.api.stock.client.fft.api.StocksInventoryApi
import dev.simonscholz.api.stock.client.fft.invoker.ApiClient
import io.github.cdimascio.dotenv.dotenv

fun main() {
    val dotenv = dotenv()

    val baseUri = dotenv["baseUri"]
    val bearerToken = dotenv["bearerToken"]

    val apiClient = ApiClient()
    apiClient.updateBaseUri(baseUri)
    apiClient.setRequestInterceptor {
        it.header("Authorization", "Bearer $bearerToken")
    }

    val api = StocksInventoryApi(apiClient)

    val stocks =
        api.getStocks(
            "ddf30f73-8a37-46bb-b7b8-e20ddc43105c",
            null,
            listOf("10272261"),
            null,
            null,
            null,
        )

    println(stocks)
}
