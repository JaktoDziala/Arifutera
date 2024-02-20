package com.example.Arifutera.github;

import com.example.Arifutera.github.DTOs.GitHubResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Set;

@RestController
public class GitHubController {

    private final GitHubService<Flux<GitHubResponseDTO>> gitHubServiceWebFlux;
    private final GitHubService<Set<GitHubResponseDTO>> gitHubServiceRestClient;


    public GitHubController(@Qualifier("webFlux") GitHubService<Flux<GitHubResponseDTO>> gitHubServiceWebFlux,
                            @Qualifier("restClient") GitHubService<Set<GitHubResponseDTO>> gitHubServiceRestClient
    ) {
        this.gitHubServiceWebFlux = gitHubServiceWebFlux;
        this.gitHubServiceRestClient = gitHubServiceRestClient;
    }

    @GetMapping("/web-flux/user/{username}")
    ResponseEntity<Flux<GitHubResponseDTO>> getRepositoriesWebFlux(@PathVariable String username) {
        return ResponseEntity.ok(gitHubServiceWebFlux.getRepositories(username));

    }


    @GetMapping("/rest-client/user/{username}")
    ResponseEntity<Set<GitHubResponseDTO>> getRepositoriesRestClient(@PathVariable String username) {
        return ResponseEntity.ok(gitHubServiceRestClient.getRepositories(username));

    }
}
