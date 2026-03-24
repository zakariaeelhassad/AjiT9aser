package com.example.backend.service;

import org.springframework.boot.CommandLineRunner;

public interface DataInitializationService extends CommandLineRunner {

    @Override
    void run(String... args) throws Exception;
}
