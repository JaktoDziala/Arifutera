package com.example.Arifutera.github;

import com.example.Arifutera.github.DTOs.GitHubResponseDTO;
import com.example.Arifutera.util.JsonUtil;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@WireMockTest(httpPort = 8081)
class GitHubRepositoriesReactiveFlowIT {
    public final static String INVALID_USERNAME = "NotFound";
    public final static String VALID_USERNAME = "JaktoDziala";
    public final static String VALID_REPOSITORY = "ArifuteraNonFork";

    @Autowired
    WebTestClient webTestClient;


    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("github.api.base-url", () -> "http://localhost:8081");
    }

    @Test
    void getRepositoriesRestClient_withValidUsername_returnsNonForkRepositories() throws IOException {
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

        webTestClient.get()
                .uri("/web-flux/user/{username}", "JaktoDziala")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GitHubResponseDTO.class)
                .hasSize(1)
                .consumeWith(response -> {
                    List<GitHubResponseDTO> gitHubResponseDTOS = response.getResponseBody();
                    assertAll(
                            () -> assertNotNull(gitHubResponseDTOS),
                            () -> assertEquals(JsonUtil.sanitizeJson(expectedResult), JsonUtil.toJson(gitHubResponseDTOS))
                    );
                });
    }

    @Test
    void getRepositoriesRestClient_withValidUsernameAndNoRepositories_returnsEmptySet() throws IOException {
        String responseBodyRepositories = IOUtils.resourceToString("/jsons/github/response/empty.json", StandardCharsets.UTF_8);

        stubFor(get(urlPathEqualTo("/users/" + VALID_USERNAME + "/repos")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(responseBodyRepositories)));

        webTestClient.get().uri("web-flux/user/{username}", VALID_USERNAME)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GitHubResponseDTO.class)
                .hasSize(0);
    }

    @Test
    void getRepositoriesRestClient_withInvalidUsername_returnNotFoundResponse() throws IOException {
        String responseBodyNotFound = IOUtils.resourceToString("/jsons/github/response/not-found.json", StandardCharsets.UTF_8);
        stubFor(get(urlPathEqualTo("/users/" + INVALID_USERNAME + "/repos")).willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody(responseBodyNotFound)));

    webTestClient.get().uri("/web-flux/user/{username}", INVALID_USERNAME)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody().json(responseBodyNotFound);
    }
}