package dev.simonscholz

import io.smallrye.mutiny.Multi
import io.vertx.mutiny.sqlclient.Pool
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class DatabaseRepository(
    private val client: Pool,
) {
    fun queryDB(): Multi<String> =
        client
            .query("select * from quarkus")
            .execute()
            .onItem()
            .transformToMulti {
                Multi.createFrom().iterable(it)
            }.onItem()
            .transform {
                val name = it.getString("name")
                "$name\n"
            }
}
