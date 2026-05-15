package dev.simonscholz

import io.smallrye.mutiny.Multi
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.flywaydb.core.internal.database.base.Database

@Path("/api")
class GreetingResource(
    private val database: DatabaseRepository,
    private val dummyService: DummyService,
) {
    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    fun hello() = "Hello from Quarkus REST"

    @GET
    @Path("/db")
    @Produces(MediaType.TEXT_PLAIN)
    fun db(): Multi<String> = database.queryDB()

    @GET
    @Path("/dummy")
    @Produces(MediaType.TEXT_PLAIN)
    fun dummy(): String = dummyService.dummy()
}
