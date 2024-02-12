package com.example.Atipera.github;

import com.example.Atipera.github.DTOs.GitHubResponseDTO;

import java.util.Set;

public interface GitHubService {
    Set<GitHubResponseDTO> getRepositories(String username);
}
