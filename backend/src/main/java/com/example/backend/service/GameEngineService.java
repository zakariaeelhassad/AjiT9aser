package com.example.backend.service;

import com.example.backend.dto.game.GameStateResponse;

public interface GameEngineService {

    void initialize();

    void simulateMatchMinute();

    void startEngine();

    void stopEngine();

    void resetEngine();

    GameStateResponse getGameState();

    boolean isGameweekActive();
}
