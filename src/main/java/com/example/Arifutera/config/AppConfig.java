package com.example.Arifutera.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {
    @Value("${github.api.base-url}")
    private String gitHubApiBaseUrl;


    @Bean
    public ExecutorService executorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    // region Beans for calling REST endpoints

    // ASYNCHRONOUS
    @Bean
    public WebClient webClient() {
        return WebClient.create("https://api.github.com");
    }

    // SYNCHRONOUS
    @Bean
    public RestClient restClient() {
        return RestClient.create(gitHubApiBaseUrl);
    }
    // endregion

    // region TODOs
    // TODO: Virtual threds vs reactive programming (how to combine them, if worth it)
    // TODO: Exception handling for reactive
    // TODO: When to choose reactive over imperative (WebClient vs RestClient)
    // TODO: Add proxy to not get blocked from accessing github api on spam? This or cache option, or other solution
    // endregion
}
