package com.example.Arifutera.github.Impl;

import com.example.Arifutera.exceptions.DataProcessingException;
import com.example.Arifutera.exceptions.ResourceNotFoundException;
import com.example.Arifutera.github.DTOs.BranchDTO;
import com.example.Arifutera.github.DTOs.GitHubResponseDTO;
import com.example.Arifutera.github.DTOs.RepositoryDTO;
import com.example.Arifutera.github.GitHubService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;


@Service
public class GitHubServiceRestClientImpl implements GitHubService<Set<GitHubResponseDTO>> {

    private final RestClient restClient;

    GitHubServiceRestClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }

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
        } catch (HttpClientErrorException e) {
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

    // TODO: Start from here, fix, add error handling
    //  https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#_error_handling
    Set<BranchDTO> fetchRepositoryBranches(String username, String repositoryName) {
        return restClient.get()
                .uri("/repos/{username}/{repositoryName}/branches", username, repositoryName)
                .retrieve()
                .toEntity(Set < BranchDTO >);

        String url = gitHubApiBaseUrl + "/repos/" + username + "/" + repositoryName + "/branches";
        String json;
        try {
            json = restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException e) {
            throw new ResourceNotFoundException(String.format("Repository " + repositoryName + " could not be found under " + username + " user! " +
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