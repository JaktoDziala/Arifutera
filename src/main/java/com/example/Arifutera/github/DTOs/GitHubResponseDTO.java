package com.example.Arifutera.github.DTOs;

import java.util.Set;

public record GitHubResponseDTO(String repositoryName, String loginName, Set<BranchDTO> branches) {
}
