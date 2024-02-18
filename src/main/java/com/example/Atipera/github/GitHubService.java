package com.example.Atipera.github;

import com.example.Atipera.github.DTOs.GitHubResponseDTO;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface GitHubService {
    Set<GitHubResponseDTO> getRepositories(String username);
    Flux<GitHubResponseDTO> getRepositoriesWebflux(String username);
}
