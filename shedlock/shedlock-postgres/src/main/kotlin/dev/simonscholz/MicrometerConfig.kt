package dev.simonscholz

import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.config.MeterFilter
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton
import org.jboss.logging.Logger

@Singleton
class MicrometerConfig(
    private val logger: Logger,
) {
    @Singleton
    @Produces
    fun allowOnlyCertainMetrics(): MeterFilter =
        MeterFilter.denyUnless { id: Meter.Id ->
            logger.info("Filtering $id")
            id.name in
                setOf(
                    "http.server.requests",
                    "http.client.requests",
                    "ft.circuitbreaker.state",
                    "scheduled.methods",
                )
        }
}
