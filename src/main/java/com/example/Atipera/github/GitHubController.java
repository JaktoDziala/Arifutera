package com.example.Atipera.github;

import com.example.Atipera.exceptions.InvalidRequestDataException;
import com.example.Atipera.github.DTOs.GitHubResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Set;

@RestController
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/user/{username}")
    ResponseEntity<Set<GitHubResponseDTO>> getRepositories(@PathVariable String username) throws IOException {
        if (username.isBlank()){
            throw new InvalidRequestDataException("Username cannot be blank!");
        }

        return new ResponseEntity<>(gitHubService.getRepositories(username), HttpStatus.OK);
    }
}
