package com.example.demo.service;

import com.example.demo.configuration.MqttConfig;
import com.example.demo.dto.CommandRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandService {

    private final MqttConfig.MqttOutboundGateway mqttOutboundGateway;
    private final ObjectMapper objectMapper; // Dùng để chuyển đổi DTO sang JSON String

    /**
     * Gửi một lệnh điều khiển xuống thiết bị IoT qua MQTT.
     *
     * @param deviceUid ID duy nhất của thiết bị đích.
     * @param command CommandRequestDTO chứa hành động và payload.
     */
    synchronized public void sendCommand(String deviceUid, CommandRequestDTO command) {

        // 1. Tạo payload từ CommandRequestDTO
        // Tạo một cấu trúc dữ liệu đơn giản hơn để gửi qua MQTT nếu cần,
        // nhưng ở đây ta gửi toàn bộ DTO sau khi đã chuẩn hóa.
        String jsonPayload;
        try {
            // ObjectMapper chuyển đổi CommandRequestDTO (action, payload) thành chuỗi JSON
            jsonPayload = objectMapper.writeValueAsString(command);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert command DTO to JSON for device {}: {}", deviceUid, e.getMessage());
            throw new RuntimeException("Error serializing command payload.", e);
        }

        // 2. Tính toán topic gửi
        // Cấu trúc topic: smartgarden/command/{deviceUid}
        // (Hoặc smartgarden/device/{deviceUid}/command, tùy theo giao thức thống nhất với ESP32)
        final String commandTopic = String.format("smartgarden/device/%s/command", deviceUid);

        log.info("Sending command to topic: {} with payload: {}", commandTopic, jsonPayload);

        // 3. Gọi interface MqttOutboundGateway
        // Gateway sẽ tự động gửi tin nhắn này vào kênh MQTT đã cấu hình.
        mqttOutboundGateway.sendToMqtt(jsonPayload, commandTopic);
    }
}