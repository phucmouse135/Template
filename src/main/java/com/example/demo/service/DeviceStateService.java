package com.example.demo.service;

import com.example.demo.dto.DeviceStateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceStateService {
    private final RedisTemplate<String, DeviceStateDTO> deviceStateRedisTemplate;
    private final ObjectMapper objectMapper; // Dùng để parse JSON payload từ MQTT

    // Prefix cho Key Redis
    private static final String REDIS_KEY_PREFIX = "device:state:";

    // Phương thức tạo Key cho Redis
    private String createKey(String deviceUid) {
        return REDIS_KEY_PREFIX + deviceUid;
    }

    /**
     * 1. Lấy trạng thái tức thời của thiết bị từ Redis.
     */
    public DeviceStateDTO getState(String deviceUid) {
        String key = createKey(deviceUid);
        DeviceStateDTO state = deviceStateRedisTemplate.opsForValue().get(key);

        // Trả về DTO nếu tìm thấy, hoặc một trạng thái mặc định/rỗng (tùy vào yêu cầu)
        return Optional.ofNullable(state).orElseGet(() -> DeviceStateDTO.builder()
                .deviceUid(deviceUid)
                .status("UNKNOWN")
                .lastSeen(0)
                .build());
    }

    public DeviceStateDTO updateStateFromMqtt(String deviceUid, String messageType, String payload) {
        String key = createKey(deviceUid);
        DeviceStateDTO currentState = getState(deviceUid);

        try {
            switch (messageType.toLowerCase()) {
                case "telemetry":
                    // Giả định payload là JSON chứa dữ liệu cảm biến
                    DeviceStateDTO.SensorData sensorData = objectMapper.readValue(payload, DeviceStateDTO.SensorData.class);
                    currentState.setSensors(sensorData);
                    currentState.setLastSeen(System.currentTimeMillis());
                    break;
                case "status":
                    // Giả định payload là "ONLINE" hoặc "OFFLINE"
                    currentState.setStatus(payload);
                    currentState.setLastSeen(System.currentTimeMillis());
                    break;
                // Xử lý các loại messageType khác nếu cần
                default:
                    log.warn("Unknown message type: {}", messageType);
            }

            // Lưu trạng thái cập nhật trở lại Redis
            deviceStateRedisTemplate.opsForValue().set(key, currentState);
            log.debug("Updated device state in Redis for deviceUid={}: {}", deviceUid, currentState);

        } catch (Exception e) {
            log.error("Failed to update device state from MQTT for deviceUid={}: {}", deviceUid, e.getMessage());
        }

        return currentState;
    }

}
