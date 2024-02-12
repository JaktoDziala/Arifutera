package com.example.Atipera.github.DTOs;

import java.util.Set;

public interface GitHubService {
    Set<GitHubResponseDTO> getRepositories(String username);
    Set<RepositoryDTO> fetchUserNonForkRepositories(String username);
    Set<BranchDTO> fetchRepositoryBranches(String username, String repositoryName);
}
