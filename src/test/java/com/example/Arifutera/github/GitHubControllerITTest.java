package com.example.Arifutera.github;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest()
class GitHubControllerITTest {

    // TODO: https://wiremock.org/docs/stubbing/
    //  https://github.com/spring-projects/spring-framework/blob/main/spring-webflux/src/test/java/org/springframework/web/reactive/function/client/WebClientIntegrationTests.java
    //  https://github.com/spring-projects/spring-framework/blob/main/spring-web/src/test/java/org/springframework/web/client/RestClientIntegrationTests.java
    //  https://www.youtube.com/watch?v=jhhi03AIin4
    //  https://geek.justjoin.it/testy-integracyjne-z-wykorzystaniem-wiremock/

    @Autowired
    GitHubController sut;

    public static String VALID_USERNAME = "JaktoDzialaA";

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("github.url", () -> "https://api.github.com");
    }



    @Test
    void getRepositoriesRestClient() {
        // given
        stubFor(get("/rest-client/user/" + VALID_USERNAME)
                .willReturn(ok()));

        sut.getRepositoriesRestClient(VALID_USERNAME);
    }

}