package dev.simonscholz

import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.ScheduledExecution
import net.javacrumbs.shedlock.cdi.SchedulerLock

class Scheduler {

    @Scheduled(every = "40s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    @SchedulerLock(name = "myTask", lockAtLeastFor = "PT30S", lockAtMostFor = "PT50S")
    fun myTask(scheduledExecution: ScheduledExecution) {
        println("""
            Running Scheduler Task.
            Firetime: ${scheduledExecution.fireTime}
            Scheduled Firetime: ${scheduledExecution.scheduledFireTime}
            Trigger: ${scheduledExecution.trigger}
            """)
    }
}
