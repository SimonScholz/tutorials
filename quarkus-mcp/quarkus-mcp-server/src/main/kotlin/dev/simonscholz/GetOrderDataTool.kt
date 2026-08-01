package dev.simonscholz

import io.quarkiverse.mcp.server.McpLog
import io.quarkiverse.mcp.server.Tool
import io.quarkiverse.mcp.server.ToolArg
import io.quarkus.security.Authenticated
import io.smallrye.common.annotation.RunOnVirtualThread
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.eclipse.microprofile.jwt.Claims
import org.eclipse.microprofile.jwt.JsonWebToken
import java.math.BigDecimal
import java.time.LocalDate

@Singleton
class GetOrderDataTool {
    @Inject
    lateinit var accessToken: JsonWebToken

    private val orders =
        listOf<Order>(
            Order(
                orderNumber = "12345",
                totalPrice = BigDecimal("99.99"),
                date = LocalDate.now().minusDays(1),
                customerEmail = "alice@example.com",
                lineItems =
                    listOf(
                        LineItem("Product A", 1, BigDecimal("49.99")),
                        LineItem("Product B", 2, BigDecimal("20.00")),
                        LineItem("Product C", 2, BigDecimal("5.00")),
                    ),
            ),
            Order(
                orderNumber = "67890",
                totalPrice = BigDecimal("299.99"),
                date = LocalDate.now(),
                customerEmail = "alice@example.com",
                lineItems =
                    listOf(
                        LineItem("Product A", 1, BigDecimal("49.99")),
                        LineItem("Product B", 2, BigDecimal("25.00")),
                        LineItem("Product C", 4, BigDecimal("50.00")),
                    ),
            ),
            Order(
                orderNumber = "ABCDE",
                totalPrice = BigDecimal("199.99"),
                date = LocalDate.now(),
                customerEmail = "bob@example.com",
                lineItems =
                    listOf(
                        LineItem("Product A", 1, BigDecimal("49.99")),
                        LineItem("Product B", 2, BigDecimal("25.00")),
                        LineItem("Product C", 5, BigDecimal("20.00")),
                    ),
            ),
        )

    @Authenticated
    @Tool(name = "lastOrderData", description = "Gets data about the user´s last order.")
    @RunOnVirtualThread
    fun lastOrderData(log: McpLog): GetOrderResult {
        val email: String = accessToken.getClaim(Claims.email.name)
        log.info("Finding order for user $email") // you never want to have email in your logs ;)
        val order = findLastOrderOfUser(email)
        log.info("Found order $order for user $email") // you never want to have email in your logs ;)
        return order
    }

    @Authenticated
    @Tool(name = "orderDataByOrderNumber", description = "Gets data about an order for a user by orderNumber.")
    @RunOnVirtualThread
    fun orderDataByOrderNumber(
        @ToolArg(description = "orderNumber") orderNumber: String,
        log: McpLog,
    ): GetOrderResult {
        val email: String = accessToken.getClaim(Claims.email.name)
        log.info("Finding order for user $email") // you never want to have email in your logs ;)
        val order = finOrderByOrderNumber(email, orderNumber)
        log.info("Found order $order for user $email by looking for orderNumber $orderNumber") // you never want to have email in your logs ;)
        return order
    }

    private fun findLastOrderOfUser(email: String): GetOrderResult =
        orders.sortedBy { it.date }.lastOrNull { it.customerEmail == email }?.let {
            OrderFound(it)
        } ?: OrderNotFound("No orders found for user with email $email")

    private fun finOrderByOrderNumber(
        email: String,
        orderNumber: String,
    ): GetOrderResult =
        orders.sortedBy { it.date }.lastOrNull { it.customerEmail == email && it.orderNumber == orderNumber }?.let {
            OrderFound(it)
        } ?: OrderNotFound(orderNumber).takeIf { orders.none { order -> order.orderNumber == orderNumber } } ?: OrderAccessDenied(
            orderId = orderNumber,
        )
}

sealed interface GetOrderResult

data class OrderFound(
    val order: Order,
) : GetOrderResult

data class OrderAccessDenied(
    val orderId: String,
    val message: String = "You are not allowed to access this order.",
) : GetOrderResult

data class OrderNotFound(
    val orderId: String,
    val message: String = "Order not found.",
) : GetOrderResult

data class Order(
    val orderNumber: String,
    val totalPrice: BigDecimal,
    val date: LocalDate,
    val customerEmail: String,
    val lineItems: List<LineItem>,
)

data class LineItem(
    val productName: String,
    val quantity: Int,
    val price: BigDecimal,
)
