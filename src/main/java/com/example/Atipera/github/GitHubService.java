package com.example.Atipera.github;

import com.example.Atipera.exceptions.ResourceNotFoundException;
import com.example.Atipera.github.DTOs.BranchDTO;
import com.example.Atipera.github.DTOs.GitHubResponseDTO;
import com.example.Atipera.github.DTOs.RepositoryDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class GitHubService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final RestTemplate restTemplate;

    @Autowired
    public GitHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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

    public Set<RepositoryDTO> fetchUserNonForkRepositories(String username) {
        String url;
        url = "https://api.github.com/users/" + username + "/repos";
        String json;
        try {
             json = restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException e){
            throw new ResourceNotFoundException(String.format("Username " + username + " could not be found!"));
        }

        try {
            Set<RepositoryDTO> repositoryDTOS = OBJECT_MAPPER.readValue(json, new TypeReference<>() {
            });
            return repositoryDTOS.stream()
                    .filter(repositoryDTO -> !repositoryDTO.fork())
                    .collect(Collectors.toSet());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<BranchDTO> fetchRepositoryBranches(String username, String repositoryName) {
        String url = "https://api.github.com/repos/" + username + "/" + repositoryName + "/branches";
        String json;
        try {
            json = restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException e){
            throw new ResourceNotFoundException(String.format("Repository " + repositoryName + " could not be found under "+ username + " user! " +
                    "Check if repository exists or has public visibility."));
        }

        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
