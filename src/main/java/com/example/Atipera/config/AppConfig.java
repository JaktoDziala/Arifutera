package com.example.Atipera.config;

import com.example.Atipera.exceptions.SetupException;
import org.kohsuke.github.GitHub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class AppConfig {
    @Bean
    public GitHub gitHub() throws IOException{
        try {
            return GitHub.connectAnonymously();
        } catch (IOException ignore) {
            throw new SetupException("Failed to anonymously connect into GitHub API!");
        }
    }
}
