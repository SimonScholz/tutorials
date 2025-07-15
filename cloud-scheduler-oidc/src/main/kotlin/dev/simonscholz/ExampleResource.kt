package dev.simonscholz

import io.quarkus.security.Authenticated
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jboss.logging.Logger

@Path("/api")
class ExampleResource(
    private val logger: Logger,
) {
    @POST
    @Path("/example")
    @Authenticated
    fun example(
        @HeaderParam("Authorization") token: String?,
        @QueryParam("country") country: String,
    ): Response {
        logger.info { "Authorization $token" }

        CoroutineScope(Dispatchers.IO).launch {
            // call a service and pass country from request
        }

        return Response.accepted().build()
    }
}
