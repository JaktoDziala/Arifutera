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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class GitHubServiceImpl implements GitHubService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String gitHubApiBaseUrl;

    GitHubServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper, @Value("${github.api.base-url}") String gitHubApiBaseUrl) {
        this.restTemplate = restTemplate;
        this.gitHubApiBaseUrl = gitHubApiBaseUrl;
        this.objectMapper = objectMapper;
    }

    /**
     TTL of @Cacheable - 120s
     */
    @Cacheable(value = "repositories", key = "#username")
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
