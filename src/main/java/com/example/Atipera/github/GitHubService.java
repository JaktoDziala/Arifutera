package com.example.Atipera.github;

import com.example.Atipera.github.DTOs.BranchDTO;
import com.example.Atipera.github.DTOs.GitHubResponseDTO;
import com.example.Atipera.github.DTOs.RepositoryDTO;

import java.util.Set;

public interface GitHubService {
    Set<GitHubResponseDTO> getRepositories(String username);
    Set<RepositoryDTO> fetchUserNonForkRepositories(String username);
    Set<BranchDTO> fetchRepositoryBranches(String username, String repositoryName);
}
