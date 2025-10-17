package dev.simonscholz

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/hello")
class GreetingResource(
    private val dummyService: DummyService,
) {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello() = dummyService.dummy()
}
