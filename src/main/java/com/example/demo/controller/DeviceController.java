package com.example.demo.controller;

import com.example.demo.configuration.CustomUserDetails;
import com.example.demo.dto.CommandRequestDTO;
import com.example.demo.dto.DeviceDto;
import com.example.demo.dto.DeviceStateDTO;
import com.example.demo.dto.TelemetryLogDto;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Device Controller")
public class DeviceController {
    private final UserService userService;
    private final DeviceService deviceService;
    private final DeviceStateService deviceStateService; // Cache Redis
    private final TelemetryService telemetryService;     // Lịch sử MySQL
    private final CommandService commandService;

    @Operation(summary = "Đăng ký (claim) một thiết bị mới bằng UID của nó")
    @PostMapping("/claim")
    public ResponseEntity<ApiResponse<DeviceDto>> claimDevice(
            @RequestParam String deviceUid,
            // Lấy thông tin người dùng đã đăng nhập từ Security Context
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        DeviceDto DeviceDto = deviceService.claimDevice(deviceUid, userDetails.getUser());

        return ResponseEntity.ok(ApiResponse.<DeviceDto>builder()
                .code(200)
                .message("Device claimed successfully.")
                .data(DeviceDto)
                .build());
    }

    // 2. ENDPOINT: Lấy danh sách các thiết bị CỦA TÔI
    @Operation(summary = "Lấy danh sách các thiết bị mà người dùng hiện tại sở hữu")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeviceDto>>> getMyDevices(
            // Lấy ID người dùng đã đăng nhập
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<DeviceDto> devices = deviceService.getDevicesByUser(userDetails.getId());

        return ResponseEntity.ok(ApiResponse.<List<DeviceDto>>builder()
                .code(200)
                .message("Successfully retrieved user's devices.")
                .data(devices)
                .build());
    }

    // 3. ENDPOINT: Lấy trạng thái TỨC THỜI của thiết bị (từ Cache)
    @Operation(summary = "Lấy trạng thái TỨC THỜI (real-time) của thiết bị (từ Redis Cache)")
    @GetMapping("/{deviceUid}/state")
    public ResponseEntity<ApiResponse<DeviceStateDTO>> getDeviceState(
            @PathVariable String deviceUid,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws AccessDeniedException {

        // BẢO MẬT: Kiểm tra quyền sở hữu trước khi truy cập
        deviceService.validateDeviceOwnership(deviceUid, userDetails.getId());

        DeviceStateDTO state = deviceStateService.getState(deviceUid);

        return ResponseEntity.ok(ApiResponse.<DeviceStateDTO>builder()
                .code(200)
                .message("Successfully retrieved device state.")
                .data(state)
                .build());
    }

    // 4. ENDPOINT: Lấy lịch sử dữ liệu (từ CSDL)
    @Operation(summary = "Lấy lịch sử dữ liệu cảm biến trong khoảng thời gian (từ MySQL)")
    @GetMapping("/{deviceUid}/history")
    public ResponseEntity<ApiResponse<List<TelemetryLogDto>>> getDeviceHistory(
            @PathVariable String deviceUid,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) throws AccessDeniedException {

        // BẢO MẬT: Kiểm tra quyền sở hữu
        deviceService.validateDeviceOwnership(deviceUid, userDetails.getId());

        // Cần giả định phương thức này tồn tại trong TelemetryService
        List<TelemetryLogDto> history = telemetryService.getHistory(deviceUid, from, to);

        return ResponseEntity.ok(ApiResponse.<List<TelemetryLogDto>>builder()
                .code(200)
                .message("Successfully retrieved device history.")
                .data(history)
                .build());
    }

    // 5. ENDPOINT: Gửi một lệnh điều khiển xuống thiết bị
    @Operation(summary = "Gửi một lệnh điều khiển xuống thiết bị qua MQTT Outbound")
    @PostMapping("/{deviceUid}/command")
    public ResponseEntity<ApiResponse<Void>> sendCommand(
            @PathVariable String deviceUid,
            @Valid @RequestBody CommandRequestDTO command,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws AccessDeniedException {

        // BẢO MẬT: Kiểm tra quyền sở hữu
        deviceService.validateDeviceOwnership(deviceUid, userDetails.getId());

        commandService.sendCommand(deviceUid, command);

        // Trả về HTTP 202 Accepted (Đã chấp nhận yêu cầu)
        return ResponseEntity.accepted().body(ApiResponse.<Void>builder()
                .code(202)
                .message("Command sent successfully to device.")
                .build());
    }
}
