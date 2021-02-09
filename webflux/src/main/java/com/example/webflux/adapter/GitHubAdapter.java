package com.example.webflux.adapter;

import com.github.api.GithubApi;
import com.github.client.ApiClient;
import com.github.dto.ContributorDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class GitHubAdapter {

    @CircuitBreaker(name = "getContributors")
    public Flux<ContributorDTO> getTutorialsContributor() {
        ApiClient apiClient = new ApiClient();
        GithubApi githubApi = new GithubApi(apiClient);
        return githubApi.getContributors("SimonScholz", "tutorials", 10, 1);
    }
}
