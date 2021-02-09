package com.example.webflux.service;

import com.example.dto.DeveloperDTO;
import com.example.dto.MoneyDTO;
import com.example.webflux.adapter.GitHubAdapter;
import com.github.dto.ContributorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class DeveloperService {

    private final GitHubAdapter gitHubAdapter;

    public Flux<DeveloperDTO> getListOfDevelopers() {
        Flux<ContributorDTO> resilience4JContributor = gitHubAdapter.getTutorialsContributor();
        return resilience4JContributor.filter(c -> "simonscholz".equalsIgnoreCase(c.getLogin()))
                .map(DeveloperService::createDeveloper);
    }

    private static DeveloperDTO createDeveloper(ContributorDTO c) {
        return new DeveloperDTO()
                .name("Simon Scholz")
                .role("Senior Developer")
                .commits(c.getContributions().intValue())
                .salary(new MoneyDTO().amount(new BigDecimal(2)).currency("EUR"));
    }
}
