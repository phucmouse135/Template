package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GardenAutomationService {
    private final AiChatService aiChatService;


    private static final String DEFAULT_DEVICE_UID = "ESP32_GARDEN_01";
    private static final String DEFAULT_LOCATION = "Hanoi,VN";

    @Scheduled(cron = "0 0 */5 * * *")
    public void runProactiveAutomation() {
        log.info("GardenAutomationService runProactiveAutomation");
        try{
            String aiDecision = aiChatService.getChatResponse(DEFAULT_DEVICE_UID, DEFAULT_LOCATION);
            log.info("GardenAutomationService AI Decision: " + aiDecision);
        }
        catch (Exception e){
            log.error("GardenAutomationService runProactiveAutomation error: " + e.getMessage());
        }
        log.info("GardenAutomationService runProactiveAutomation");
    }
}
