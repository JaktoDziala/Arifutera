package com.example.Arifutera.github;

import com.example.Arifutera.util.JsonUtil;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8081)
class GitHubRepositoriesFlowIT {

    @Autowired
    GitHubController sut;

    public static final String INVALID_USERNAME = "NotFound";
    public static final String VALID_USERNAME = "JaktoDziala";
    public static final String VALID_REPOSITORY = "ArifuteraNonFork";

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("github.api.base-url", () -> "http://localhost:8081");
    }

    @Test
    void getRepositoriesRestClient_withValidUsername_returnsNonForkRepositories() throws IOException {
        // given
        String responseBodyRepositories = IOUtils.resourceToString("/jsons/github/response/repository/all-repositories.json", StandardCharsets.UTF_8);
        String responseBodyBranches = IOUtils.resourceToString("/jsons/github/response/branches.json", StandardCharsets.UTF_8);
        String expectedResult = IOUtils.resourceToString("/jsons/github/response/full-response.json", StandardCharsets.UTF_8);

        stubFor(get(urlPathEqualTo("/users/" + VALID_USERNAME + "/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyRepositories)));

        stubFor(get(urlPathEqualTo(String.format("/repos/%s/%s/branches", VALID_USERNAME, VALID_REPOSITORY)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyBranches)));

        // when
        var response = sut.getRepositoriesRestClient(VALID_USERNAME);

        // then
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(JsonUtil.sanitizeJson(expectedResult), JsonUtil.toJson(response.getBody()))
        );
    }

    @Test
    void getRepositoriesRestClient_withValidUsernameAndNoRepositories_returnsEmptySet() throws IOException {
        // given
        String responseBodyRepositories = IOUtils.resourceToString("/jsons/github/response/empty.json", StandardCharsets.UTF_8);
        stubFor(get(urlPathEqualTo("/users/" + VALID_USERNAME + "/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyRepositories)));

        // when
        var response = sut.getRepositoriesRestClient(VALID_USERNAME);

        // then
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertTrue(Objects.requireNonNull(response.getBody()).isEmpty())
        );
    }

    @Test
    void getRepositoriesRestClient_withInvalidUsername_returnNotFoundResponse() throws IOException {
        // given
        String responseBodyNotFound = IOUtils.resourceToString("/jsons/github/response/not-found.json", StandardCharsets.UTF_8);
        stubFor(get(urlPathEqualTo("/users/" + INVALID_USERNAME + "/repos"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyNotFound)));

        // when then
        var exception = assertThrows(HttpClientErrorException.class, () ->
                sut.getRepositoriesRestClient(INVALID_USERNAME));

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode()),
                () -> assertEquals(responseBodyNotFound, exception.getResponseBodyAsString())
        );
    }
}