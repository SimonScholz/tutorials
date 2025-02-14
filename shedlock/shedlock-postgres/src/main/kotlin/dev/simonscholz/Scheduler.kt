package dev.simonscholz

import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.ScheduledExecution
import net.javacrumbs.shedlock.cdi.SchedulerLock
import org.jboss.logging.Logger

class Scheduler(
    private val logger: Logger,
) {
    @Scheduled(every = "40s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    @SchedulerLock(name = "myTask", lockAtLeastFor = "PT30S", lockAtMostFor = "PT50S")
    fun myTask(scheduledExecution: ScheduledExecution) {
        logger.info(
            """
            Running Scheduler Task.
            Firetime: ${scheduledExecution.fireTime}
            Scheduled Firetime: ${scheduledExecution.scheduledFireTime}
            Trigger: ${scheduledExecution.trigger}
            """,
        )
    }
}
