package com.example.Atipera.github;

import com.example.Atipera.exceptions.DataProcessingException;
import com.example.Atipera.exceptions.ResourceNotFoundException;
import com.example.Atipera.github.DTOs.BranchDTO;
import com.example.Atipera.github.DTOs.GitHubResponseDTO;
import com.example.Atipera.github.DTOs.RepositoryDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;


@Service
public class GitHubServiceImpl implements GitHubService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String gitHubApiBaseUrl;
    private final ExecutorService executorService;
    private final WebClient webClient;

    GitHubServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper, @Value("${github.api.base-url}") String gitHubApiBaseUrl, ExecutorService executorService, WebClient webClient) {
        this.restTemplate = restTemplate;
        this.gitHubApiBaseUrl = gitHubApiBaseUrl;
        this.objectMapper = objectMapper;
        this.executorService = executorService;
        this.webClient = webClient;
    }


    // REACTIVE
    @Override
    public Flux<GitHubResponseDTO> getRepositoriesWebflux(String username) {
        return fetchUserNonForkRepositoriesReactive(username)
                .flatMap(repositoryDTO -> fetchRepositoryBranchesReactive(username, repositoryDTO.name())
                        .collectList()
                        .map(branches -> new GitHubResponseDTO(repositoryDTO.name(), repositoryDTO.owner().login(), new HashSet<>(branches)))
                );
    }

    Flux<RepositoryDTO> fetchUserNonForkRepositoriesReactive(String username) {
        return webClient.get()
                .uri ("/users/{username}/repos", username)
                .retrieve()
                .bodyToFlux(RepositoryDTO.class)
                .filter(repositoryDTO -> !repositoryDTO.fork());
    }

    private Flux<BranchDTO> fetchRepositoryBranchesReactive(String username, String repositoryName) {
        return webClient.get()
                .uri("/repos/{username}/{repositoryName}/branches", username, repositoryName)
                .retrieve()
                .bodyToFlux(BranchDTO.class);
    }


    // STANDARD (Keeping for fast comparison, learning purpose
    @Override
    public Set<GitHubResponseDTO> getRepositories(String username) {
        Set<GitHubResponseDTO> gitHubResponseDTOS = new HashSet<>();

        fetchUserNonForkRepositories(username).forEach((repositoryDTO -> {
            Set<BranchDTO> branches = fetchRepositoryBranches(username, repositoryDTO.name());
            gitHubResponseDTOS.add(
                    new GitHubResponseDTO(
                            repositoryDTO.name(),
                            repositoryDTO.owner().login(),
                            branches));
        }
        ));

        return gitHubResponseDTOS;
    }

    Set<RepositoryDTO> fetchUserNonForkRepositories(String username) {
        String url = gitHubApiBaseUrl + "/users/" + username + "/repos";
        String json;
        try {
            json = restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException e){
            throw new ResourceNotFoundException(String.format("Username " + username + " could not be found!"));
        }

        try {
            Set<RepositoryDTO> repositoryDTOS = objectMapper.readValue(json, new TypeReference<>() {
            });
            return repositoryDTOS.stream()
                    .filter(repositoryDTO -> !repositoryDTO.fork())
                    .collect(Collectors.toSet());

        } catch (Exception e) {
            throw new DataProcessingException("Failed to process repository data for username: " + username + ". Check the response structure and data integrity.");
        }
    }

    Set<BranchDTO> fetchRepositoryBranches(String username, String repositoryName) {
        String url = gitHubApiBaseUrl + "/repos/" + username + "/" + repositoryName + "/branches";
        String json;
        try {
            json = restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException e){
            throw new ResourceNotFoundException(String.format("Repository " + repositoryName + " could not be found under "+ username + " user! " +
                    "Check if repository exists or has public visibility."));
        }

        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new DataProcessingException("Failed to process branch data for repository: " + repositoryName + " under username: " + username + ". Check the response structure and data integrity.");
        }
    }
}
