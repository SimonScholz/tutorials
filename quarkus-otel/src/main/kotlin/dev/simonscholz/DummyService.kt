package dev.simonscholz

import io.opentelemetry.instrumentation.annotations.WithSpan
import jakarta.enterprise.context.ApplicationScoped
import kotlin.random.Random

@ApplicationScoped
class DummyService {
    @WithSpan
    fun dummy(): String {
        val random = Random(3).nextInt()
        return when (random) {
            0 -> {
                Thread.sleep(50)
                "Slept for 50ms"
            }
            1 -> {
                Thread.sleep(100)
                "Slept for 100ms"
            }
            2 -> {
                Thread.sleep(150)
                "Slept for 150ms"
            }
            else -> {
                Thread.sleep(200)
                "Slept for 200ms"
            }
        }
    }
}
