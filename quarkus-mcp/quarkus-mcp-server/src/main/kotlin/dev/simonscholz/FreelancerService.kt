package dev.simonscholz

import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class FreelancerService {
    fun findFreelancersBySkills(skills: List<String>): List<Freelancer> =
        listOf(
            Freelancer("Simon Scholz", listOf("Kotlin", "Java", "Quarkus", "DDD")),
            Freelancer("John Go", listOf("Go", "Docker", "Kubernetes", "GCP")),
        ).filter { freelancer ->
            skills.any { skill -> freelancer.skills.map { it.lowercase() }.contains(skill.lowercase()) }
        }
}
