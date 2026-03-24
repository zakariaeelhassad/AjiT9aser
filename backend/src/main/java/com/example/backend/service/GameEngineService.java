package com.example.backend.service;

public interface GameEngineService {

    void initialize();

    void simulateMatchMinute();

    void startEngine();

    void stopEngine();

    void resetEngine();

    com.example.backend.service.Impl.GameEngineService.GameState getGameState();

    boolean isGameweekActive();
}
