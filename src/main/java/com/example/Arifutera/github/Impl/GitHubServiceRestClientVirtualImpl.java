package com.example.Arifutera.github.Impl;

import com.example.Arifutera.github.DTOs.BranchDTO;
import com.example.Arifutera.github.DTOs.GitHubResponseDTO;
import com.example.Arifutera.github.DTOs.RepositoryDTO;
import com.example.Arifutera.github.GitHubService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@Qualifier("restClientVirtual")
public class GitHubServiceRestClientVirtualImpl implements GitHubService<Set<GitHubResponseDTO>> {

    private final RestClient restClient;
    private final ExecutorService executorService;

    GitHubServiceRestClientVirtualImpl(RestClient restClient, ExecutorService executorService) {
        this.restClient = restClient;
        this.executorService = executorService;
    }

    @Override
    public Set<GitHubResponseDTO> getRepositories(String username) {
        Set<GitHubResponseDTO> gitHubResponseDTOS = new HashSet<>();
        fetchNonForkRepositories(username).forEach(repositoryDTO -> {
                    gitHubResponseDTOS.add(
                            new GitHubResponseDTO(
                                    repositoryDTO.name(),
                                    repositoryDTO.owner().login(),
                                    fetchRepositoryBranches(username, repositoryDTO.name())
                            )
                    );
                }
        );
        return gitHubResponseDTOS;
    }

    private Set<RepositoryDTO> fetchNonForkRepositories(String username) {
        try {
            Set<RepositoryDTO> repositoryDTOS = executorService.submit(() ->
                    restClient.get()
                            .uri("/users/{username}/repos", username)
                            .retrieve()
                            .body(new ParameterizedTypeReference<Set<RepositoryDTO>>() {
                            })).get();
            return repositoryDTOS.stream()
                    .filter(repositoryDTO -> !repositoryDTO.fork())
                    .collect(Collectors.toSet());

        } catch (InterruptedException | ExecutionException e) {
            throw new NoSuchElementException();
        }
    }

    private Set<BranchDTO> fetchRepositoryBranches(String username, String repositoryName) {
        try {
            return executorService.submit(() ->
                    restClient.get()
                            .uri("/repos/{username}/{repositoryName}/branches", username, repositoryName)
                            .retrieve()
                            .body(new ParameterizedTypeReference<Set<BranchDTO>>() {
                            })).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new NoSuchElementException();
        }
    }
}
