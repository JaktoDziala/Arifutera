package com.example.Arifutera.github.Impl;

import com.example.Arifutera.github.DTOs.BranchDTO;
import com.example.Arifutera.github.DTOs.GitHubResponseDTO;
import com.example.Arifutera.github.DTOs.RepositoryDTO;
import com.example.Arifutera.github.GitHubService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashSet;

/**
 * Getting GitHub API results in reactive, 'single' thread, asynchronous way.
 */
@Service
@Qualifier("webFlux")
public class GitHubServiceWebFluxImpl implements GitHubService<Flux<GitHubResponseDTO>> {
    private final WebClient webClient;

    GitHubServiceWebFluxImpl(WebClient webClient){
        this.webClient = webClient;
    }

    @Override
    public Flux<GitHubResponseDTO> getRepositories(String username) {
        return fetchNonForkRepositories(username)
                .flatMap(repositoryDTO -> fetchRepositoryBranches(username, repositoryDTO.name())
                        .collectList()
                        .map(branches -> new GitHubResponseDTO(repositoryDTO.name(), repositoryDTO.owner().login(), new HashSet<>(branches)))
                );
    }

    private Flux<RepositoryDTO> fetchNonForkRepositories(String username) {
        return webClient.get()
                .uri ("/users/{username}/repos", username)
                .retrieve()
                .bodyToFlux(RepositoryDTO.class)
                .filter(repositoryDTO -> !repositoryDTO.fork());
    }

    private Flux<BranchDTO> fetchRepositoryBranches(String username, String repositoryName) {
        return webClient.get()
                .uri("/repos/{username}/{repositoryName}/branches", username, repositoryName)
                .retrieve()
                .bodyToFlux(BranchDTO.class);
    }
}
