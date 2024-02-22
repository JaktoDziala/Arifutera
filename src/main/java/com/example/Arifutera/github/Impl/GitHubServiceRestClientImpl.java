package com.example.Arifutera.github.Impl;

import com.example.Arifutera.github.DTOs.BranchDTO;
import com.example.Arifutera.github.DTOs.GitHubResponseDTO;
import com.example.Arifutera.github.DTOs.RepositoryDTO;
import com.example.Arifutera.github.GitHubService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Qualifier("restClient")
public class GitHubServiceRestClientImpl implements GitHubService<Set<GitHubResponseDTO>> {

    private final RestClient restClient;

    GitHubServiceRestClientImpl(RestClient restClient) {
        this.restClient = restClient;
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
        Set<RepositoryDTO> repositoryDTOS = restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        // TODO: Check in IT test this collection nullability.
        //  Set<RepositoryDTO> repositoryDTOS = new HashSet() was not resolving null value exception warning
        return repositoryDTOS == null ? Collections.emptySet() : repositoryDTOS.stream()
                .filter(repositoryDTO -> !repositoryDTO.fork())
                .collect(Collectors.toSet());
    }

    // TODO: exception handling
    private Set<BranchDTO> fetchRepositoryBranches(String username, String repositoryName) {
        return restClient.get()
                .uri("/repos/{username}/{repositoryName}/branches", username, repositoryName)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
