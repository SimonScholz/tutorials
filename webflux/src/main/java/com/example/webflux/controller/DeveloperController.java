package com.example.webflux.controller;

import com.example.api.DevelopersApi;
import com.example.dto.DeveloperDTO;
import com.example.webflux.service.DeveloperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class DeveloperController implements DevelopersApi {

    private final DeveloperService developerService;

    @Override
    public Mono<ResponseEntity<Flux<DeveloperDTO>>> getDevelopers(ServerWebExchange exchange) {
        String m = null;
        m.trim();
        return Mono.just(ResponseEntity.ok(developerService.getListOfDevelopers()));
    }
}
