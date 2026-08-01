package dev.simonscholz

import io.quarkiverse.mcp.server.McpLog
import io.quarkiverse.mcp.server.Tool
import io.quarkiverse.mcp.server.ToolArg
import io.quarkus.security.Authenticated
import io.smallrye.common.annotation.RunOnVirtualThread
import jakarta.inject.Singleton

@Singleton
class FindFreelancerTool(
    private val freelancerService: FreelancerService,
) {
    @Tool(name = "freelancer", description = "Finds freelancers by their skills.")
    @RunOnVirtualThread
    fun findFreelancersBySkills(
        @ToolArg(description = "skills") skills: List<String>,
        log: McpLog,
    ): List<Freelancer> {
        log.info("Finding freelancers with skills: $skills")
        val freelancers = freelancerService.findFreelancersBySkills(skills)
        log.info("Found ${freelancers.size} freelancers with skills: ${freelancers.joinToString(",") { it.skills.joinToString(",") }}")
        return freelancers
    }
}
