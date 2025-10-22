package com.example.demo.service.impl;

import com.example.demo.dto.DeviceStateDTO;
import com.example.demo.dto.TelemetryLogDto;
import com.example.demo.model.entity.DeviceEntity;
import com.example.demo.model.entity.TelemetryLog;
import com.example.demo.repository.DeviceRepository;
import com.example.demo.repository.TelemetryRepository;
import com.example.demo.service.TelemetryService;
import com.example.demo.utils.mapper.TelemetryMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelemetryServiceImpl implements TelemetryService {
    private final TelemetryRepository telemetryLogRepository;
    private final DeviceRepository deviceRepository;
    private final TelemetryMapper telemetryMapper; // MapStruct Mapper
    /**
     * Lưu TelemetryLog vào MySQL một cách BẤT ĐỒNG BỘ.
     * Phương thức này sẽ được thực thi trên một thread riêng biệt.
     * @param updatedState DTO trạng thái thiết bị vừa được cập nhật từ MQTT/Redis.
     */
    @Override
    @Async // 👈 Đánh dấu phương thức này sẽ chạy bất đồng bộ
    @Transactional // Đảm bảo hoạt động CSDL diễn ra trong một Transaction
    public void saveTelemetryLog(DeviceStateDTO updatedState) {
        String deviceUid = updatedState.getDeviceUid();

        // BƯỚC 1: Tìm Device Entity (cần thiết để gán khóa ngoại)
        Optional<DeviceEntity> deviceOpt = deviceRepository.findByDeviceUid(deviceUid);

        if (deviceOpt.isEmpty()) {
            log.warn("Cannot save telemetry log: Device with UID {} not found in database.", deviceUid);
            return;
        }

        DeviceEntity device = deviceOpt.get();
        DeviceStateDTO.SensorData sensorData = updatedState.getSensors();

        if (sensorData == null) {
            log.warn("Cannot save telemetry log: Sensor data is missing for device {}.", deviceUid);
            return;
        }

        try {
            // BƯỚC 2: Chuyển đổi SensorData sang TelemetryLog Entity
            // Giả định TelemetryMapper có phương thức mapToLog(SensorData)
            TelemetryLog logEntity = telemetryMapper.toEntity(sensorData);

            // Cài đặt các trường quan trọng bị thiếu trong DTO
            logEntity.setDevice(device);

            // Chuyển đổi timestamp từ DTO (millis) sang LocalDateTime cho JPA
            Instant logTime = Instant.now();
            logEntity.setLogTime(logTime);

            // BƯỚC 3: Lưu Entity
            telemetryLogRepository.save(logEntity);
            log.info("Successfully saved telemetry log for device {}", deviceUid);

        } catch (Exception e) {
            // Log lỗi mà không làm dừng luồng MQTT
            log.error("ASYNCHRONOUS ERROR: Failed to save telemetry log for {}. {}", deviceUid, e.getMessage());
        }
    }

    @Override
    public List<TelemetryLogDto> getHistory(String deviceUid, Instant from, Instant to) {
        // Tìm thiết bị theo deviceUid
        Optional<DeviceEntity> deviceOpt = deviceRepository.findByDeviceUid(deviceUid);
        if (deviceOpt.isEmpty()) {
            log.warn("Device with UID {} not found when fetching telemetry history.", deviceUid);
            throw new RuntimeException("Device not found.");
        }
        DeviceEntity device = deviceOpt.get();


        // Truy vấn lịch sử từ repository
        List<TelemetryLog> logs = telemetryLogRepository
                .findAllByDeviceAndLogTimeBetween(device, from, to);

        // Chuyển đổi sang DTO
        return logs.stream()
                .map(telemetryMapper::toDto)
                .toList();
    }
}
