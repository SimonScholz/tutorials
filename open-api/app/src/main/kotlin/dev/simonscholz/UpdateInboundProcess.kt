package dev.simonscholz

import dev.simonscholz.api.client.fft.api.InboundInventoryApi
import dev.simonscholz.api.client.fft.invoker.ApiClient
import dev.simonscholz.api.client.fft.model.InboundProcessPurchaseOrderForUpsertDTO
import dev.simonscholz.api.client.fft.model.InputRequestedDateDTO
import io.github.cdimascio.dotenv.dotenv
import java.io.File
import java.time.OffsetDateTime

fun main() {
    val dotenv = dotenv()

    val baseUri = dotenv["baseUri"]
    val bearerToken = dotenv["bearerToken"]

    val apiClient = ApiClient()
    apiClient.updateBaseUri(baseUri)
    apiClient.setRequestInterceptor {
        it.header("Authorization", "Bearer $bearerToken")
    }
    val api = InboundInventoryApi(apiClient)

    // read first two columns of output.csv and print them
    File("output.csv").useLines { lines ->
        lines
            .drop(1) // skip header
            .forEach { line ->
                val cols = line.split(",")
                if (cols.size >= 2) {
                    val inboundProcessId = cols[0].trim()
                    val newOffsetDate = cols[1].trim()
                    val newOffsetDateTime = OffsetDateTime.parse(newOffsetDate)
                    val requestedDate = InputRequestedDateDTO().type(InputRequestedDateDTO.TypeEnum.TIME_POINT).value(newOffsetDateTime)
                    runInboundProcessUpdate(api, inboundProcessId, requestedDate)
                }
            }
    }
}

private fun runInboundProcessUpdate(
    api: InboundInventoryApi,
    inboundProcessId: String,
    requestedDate: InputRequestedDateDTO?,
) {
    val inboundProcess = api.getInboundProcess(inboundProcessId)

    val purchaseOrder =
        requireNotNull(inboundProcess.purchaseOrder) {
            "Purchase order must not be null for inbound process with id $inboundProcessId"
        }

    val updateDTO =
        InboundProcessPurchaseOrderForUpsertDTO()
            .version(inboundProcess.version)
            .orderDate(purchaseOrder.orderDate)
            .requestedItems(purchaseOrder.requestedItems)
            .requestedDate(requestedDate ?: purchaseOrder.requestedDate)

    val response =
        api.upsertInboundProcessPurchaseOrder(
            inboundProcessId,
            updateDTO,
        )

    println("Response from FFT:")
    println(response)
}
