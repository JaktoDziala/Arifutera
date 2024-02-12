package com.example.Atipera.github.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RepositoryDTO(String name, OwnerDTO owner, boolean fork) {
}
