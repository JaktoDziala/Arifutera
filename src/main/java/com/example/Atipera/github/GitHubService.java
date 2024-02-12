package com.example.Atipera.github;

import com.example.Atipera.github.DTOs.BranchDTO;
import com.example.Atipera.github.DTOs.GitHubResponseDTO;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class GitHubService {
    private final GitHub gitHub;

    GitHubService(GitHub gitHub) {
        this.gitHub = gitHub;
    }

    Set<GitHubResponseDTO> getRepositories(String username) throws IOException {
        GHUser ghUser = getGHUser(username);
        Set<GitHubResponseDTO> responseDTOS = new HashSet<>();
        Map<String, GHRepository> nonForkRepositories = getNonForkRepositories(ghUser.getRepositories());

        nonForkRepositories.forEach((name, repository) -> {
            Set<BranchDTO> branchDTOs = new HashSet<>();
            try {
                repository.getBranches().forEach((branchName, branch) -> {
                    branchDTOs.add(new BranchDTO(branchName, branch.getSHA1()));
                });
            } catch (IOException e) {
                throw new NoSuchElementException(e);
            }
            responseDTOS.add(new GitHubResponseDTO(
                    repository.getName(),
                    ghUser.getLogin(),
                    branchDTOs
            ));
        });

        return responseDTOS;
    }

    // Ignoring service isBlank validation since controller is currently the only point of entry in given requirements
    GHUser getGHUser(String username) throws IOException {
        try {
            return gitHub.getUser(username);
        } catch (IOException e) {
            throw new GHFileNotFoundException("Username could not be found!");
        }
    }

    Map<String, GHRepository> getNonForkRepositories(Map<String, GHRepository> repositoryMap) {
        return repositoryMap.entrySet().stream()
                .filter(repository -> !repository.getValue().isFork())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
