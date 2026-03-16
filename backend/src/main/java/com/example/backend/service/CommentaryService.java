package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentaryService {

    public String generateCommentary(String playerName, String eventType, int minute) {
        log.info("Generating commentary for: {} - {} at minute {}", playerName, eventType, minute);

        String commentary = generateFallbackCommentary(playerName, eventType, minute);
        log.info("Generated commentary: {}", commentary);

        return commentary;
    }

    private String generateFallbackCommentary(String playerName, String eventType, int minute) {
        // Moroccan Darija/Arabic commentary
        return switch (eventType.toUpperCase()) {
            case "GOAL" -> String.format("⚽ Waaaw! %s dkhel f had l'minute %d! Goooool khatar! 🔥", playerName, minute);
            case "ASSIST" -> String.format("💪 Makaynch! %s dar assist dial l'3am f minute %d! Tbarkellah 3lih!",
                    playerName, minute);
            case "RED_CARD" ->
                String.format("😱 Yallah! %s khrj mn l'terrain f minute %d! Red card!", playerName, minute);
            case "YELLOW_CARD" -> String.format("⚠️ %s akhd yellow card f minute %d. Ykhali bal!", playerName, minute);
            default -> String.format("⚽ %s f minute %d!", playerName, minute);
        };
    }
}
