package com.example.Arifutera.github;

public interface GitHubService<T> {
    T getRepositories(String username);
}
