package com.example.Atipera.github;

import com.example.Atipera.github.DTOs.GitHubResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/user/{username}")
    ResponseEntity<Set<GitHubResponseDTO>> getRepositories(@PathVariable String username) {
        return ResponseEntity.ok(gitHubService.getRepositories(username));
    }
}
