//package com.example.Arifutera.github;
//
//import com.example.Arifutera.exceptions.ResourceNotFoundException;
//import com.example.Arifutera.github.DTOs.*;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.Set;
//import java.util.concurrent.ExecutorService;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class GitHubServiceImplTest {
//
//    private GitHubServiceImpl sut;
//    @Mock
//    private RestTemplate restTemplate;
//    @Mock
//    private ObjectMapper objectMapper;
//    @Mock
//    private ExecutorService executorService;
//    private final static String VALID_USERNAME = "JaktoDziala";
//    private final static String VALID_REPOSITORY = "repo";
//    private final static String REPOSITORY_URL = "https://api.github.com/users/" + VALID_USERNAME + "/repos";
//    private final static String BRANCH_URL = "https://api.github.com/repos/" + VALID_USERNAME + "/" + VALID_REPOSITORY + "/branches";
//
//    @BeforeEach
//    void setup(){
//        sut = new GitHubServiceImpl(restTemplate, objectMapper, "https://api.github.com", executorService);
//    }
//
//    @Test
//    void getRepositories_withExistingUsernameAndNonForkRepository_returnsDataSet() throws IOException {
//        // given
//        String reposJson = "[{\"name\":\"repo\",\"owner\":{\"login\":\"JaktoDziala\"},\"fork\":false}]";
//        String branchesJson = "[{\"name\":\"main\",\"commit\":{\"sha\":\"2093489\"}}]";
//        Set<RepositoryDTO> expectedRepos = Set.of(new RepositoryDTO("repo", new OwnerDTO("JaktoDziala"), false));
//        Set<BranchDTO> expectedBranches = Set.of(new BranchDTO("main", new CommitDTO("2093489")));
//
//        when(restTemplate.getForObject(REPOSITORY_URL, String.class)).thenReturn(reposJson);
//        when(restTemplate.getForObject(BRANCH_URL, String.class)).thenReturn(branchesJson);
//        when(objectMapper.readValue(eq(reposJson), any(TypeReference.class))).thenReturn(expectedRepos);
//        when(objectMapper.readValue(eq(branchesJson), any(TypeReference.class))).thenReturn(expectedBranches);
//
//
//        // when
//        Set<GitHubResponseDTO> result = sut.getRepositories(VALID_USERNAME);
//
//        // then
//        assertEquals(1, result.size());
//        GitHubResponseDTO responseDTO = result.iterator().next();
//        assertEquals(VALID_REPOSITORY, responseDTO.repositoryName());
//        assertEquals(VALID_USERNAME, responseDTO.loginName());
//        assertTrue(responseDTO.branches().contains(new BranchDTO("main", new CommitDTO("2093489"))));
//    }
//
//    @Test
//    void getRepositories_withExistingUsernameAndForkRepository_returnsEmptySet() throws IOException {
//        // given
//        String reposJson = "[{\"name\":\"repo\",\"owner\":{\"login\":\"JaktoDziala\"},\"fork\":true}]";
//        when(restTemplate.getForObject(REPOSITORY_URL, String.class)).thenReturn(reposJson);
//        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(Collections.emptySet());
//
//        // when
//        Set<GitHubResponseDTO> result = sut.getRepositories(VALID_USERNAME);
//
//        // then
//        assertEquals(0, result.size());
//    }
//
//    @Test
//    void getRepositories_withExistingUsernameAndNoRepositories_returnsEmptySet() throws IOException {
//        // given
//        lenient().when(restTemplate.getForObject(REPOSITORY_URL, String.class)).thenReturn("[]");
//        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(Collections.emptySet());
//
//        // when
//        Set<GitHubResponseDTO> result = sut.getRepositories(VALID_USERNAME);
//
//        // then
//        assertEquals(result, Collections.emptySet());
//
//    }
//
//    @Test
//    void fetchUserNonForkRepositories_withNotFoundUsername_throwsException() {
//        // given
//        doThrow(HttpClientErrorException.class).when(restTemplate).getForObject(REPOSITORY_URL, String.class);
//
//        // when
//        // then
//        assertThrows(ResourceNotFoundException.class, () -> sut.fetchUserNonForkRepositories(VALID_USERNAME));
//    }
//
//    @Test
//    void fetchRepositoryBranches_withNotFoundRepository_throwsException() {
//        // given
//        doThrow(HttpClientErrorException.class).when(restTemplate).getForObject(BRANCH_URL, String.class);
//
//        // when
//        // then
//        assertThrows(ResourceNotFoundException.class, () -> sut.fetchRepositoryBranches(VALID_USERNAME, VALID_REPOSITORY));
//    }
//}