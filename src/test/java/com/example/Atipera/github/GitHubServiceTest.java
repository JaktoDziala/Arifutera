package com.example.Atipera.github;

import com.example.Atipera.github.DTOs.GitHubResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitHubServiceTest {

    @InjectMocks
    private GitHubService sut;

    @Mock
    private GitHub gitHub;

    private final static String VALID_USERNAME = "JaktoDziala";


    @Test
    void getRepositories_withExistingUsernameAndNonForkRepositories_returnsDataSet() throws IOException{
        // given
        GHUser ghUser = mock(GHUser.class);
        GHRepository ghRepository1 = mock(GHRepository.class);
        GHRepository ghRepository2 = mock(GHRepository.class);
        GHBranch ghBranch = mock(GHBranch.class);

        Map<String, GHRepository> dataMap = new HashMap<>();
        dataMap.put("repo1", ghRepository1);
        dataMap.put("repo2", ghRepository2);

        when(gitHub.getUser(VALID_USERNAME)).thenReturn(ghUser);
        when(ghUser.getRepositories()).thenReturn(dataMap);
        when(ghRepository1.getBranches()).thenReturn(Map.of("name1", ghBranch));
        when(ghRepository2.getBranches()).thenReturn(Map.of("name2", ghBranch));
        when(ghRepository1.getName()).thenReturn("repo1");
        when(ghRepository2.getName()).thenReturn("repo2");

        // when
         Set<GitHubResponseDTO> result = sut.getRepositories(VALID_USERNAME);

        // then
        assertEquals(2, result.size());

        List<GitHubResponseDTO> resultList = new ArrayList<>(result);
        resultList.sort(Comparator.comparing(GitHubResponseDTO::repositoryName));

        GitHubResponseDTO repo1Response = resultList.get(0);
        assertEquals("repo1", repo1Response.repositoryName());
        assertTrue(repo1Response.branches().stream().anyMatch(branch -> "name1".equals(branch.branchName())));

        GitHubResponseDTO repo2Response = resultList.get(1);
        assertEquals("repo2", repo2Response.repositoryName());
        assertTrue(repo2Response.branches().stream().anyMatch(branch -> "name2".equals(branch.branchName())));
    }

    @Test
    void getRepositories_withExistingUsernameAndNoRepositories_returnsEmptySet() throws IOException{
        // given
        GHUser ghUser = mock(GHUser.class);
        when(gitHub.getUser(VALID_USERNAME)).thenReturn(ghUser);

        // API test showed that repositories are never null. Ignoring null tests for structure
        when(ghUser.getRepositories()).thenReturn(new HashMap<>());

        // when
        Set<GitHubResponseDTO> result = sut.getRepositories(VALID_USERNAME);

        // then
        assertEquals(result, Collections.emptySet());

    }

    @Test
    void getGHUser_withExistingUsername_returnsUser() throws IOException {
        // given
        GHUser ghUser = new GHUser();
        when(gitHub.getUser(VALID_USERNAME)).thenReturn(ghUser);

        // when
        GHUser result = sut.getGHUser(VALID_USERNAME);

        // then
        assertSame(ghUser, result);
    }

    @Test
    void getGHUser_withNotExistingUsername_throwsException() throws IOException {
        // given
        when(gitHub.getUser("x")).thenThrow(IOException.class);

        // when
        // then
        assertThrows(GHFileNotFoundException.class, () -> sut.getGHUser("x"));

    }

    @Test
    void getNonForkRepositories_withEmptyMap_returnsEmptyMap() {
        // given
        // when
        var result = sut.getNonForkRepositories(new HashMap<>());

        // then
        assertEquals(0, result.size());
    }

    @Test
    void getNonForkRepositories_withNonForkRepositories_returnsNonForkRepositories(){
        // given
        Map<String, GHRepository> dataMap = new HashMap<>();
        dataMap.put("repo1", new GHRepository());
        dataMap.put("repo2", new GHRepository());

        // when
        var result = sut.getNonForkRepositories(dataMap);

        // then
        assertEquals(2, result.size());
    }

    @Test

    void getNonForkRepositories_withForkRepository_returnsOnlyNonForkRepositories(){
        // given
        GHRepository mockedRepository = mock(GHRepository.class);
        Map<String, GHRepository> dataMap = new HashMap<>();
        dataMap.put("repo1", new GHRepository());
        dataMap.put("repo2", mockedRepository);

        when(mockedRepository.isFork()).thenReturn(true);

        // when
        var result = sut.getNonForkRepositories(dataMap);

        // then
        assertEquals(1, result.size());
    }
}