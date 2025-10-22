package com.example.demo.service.impl;

import com.example.demo.dto.DeviceDto;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.entity.DeviceEntity;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.repository.DeviceRepository;
import com.example.demo.service.DeviceService;
import com.example.demo.utils.mapper.DeviceMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper; // MapStruct Mapper

    /**
     * 1. Đăng ký (Claim) một thiết bị mới bằng UID của nó.
     * Kiểm tra xem thiết bị đã được claim chưa và gán cho người dùng hiện tại.
     */
    @Transactional
    public DeviceDto claimDevice(String deviceUid, UserEntity user) {

        // Tìm kiếm thiết bị theo UID
        Optional<DeviceEntity> existingDevice = deviceRepository.findByDeviceUid(deviceUid);

        if (existingDevice.isPresent()) {
            DeviceEntity device = existingDevice.get();
            // Thiết bị đã tồn tại, kiểm tra xem nó đã có chủ chưa
            if (device.getUser() != null) {
                if (device.getUser().getId().equals(user.getId())) {
                    // Thiết bị đã được claim bởi chính người dùng này, trả về thông tin
                    return deviceMapper.toDto(device);
                }
                // Thiết bị đã bị người khác claim
                throw new AppException(ErrorCode.DEVICE_ALREADY_CLAIMED);
            }

            // Thiết bị tồn tại nhưng chưa có chủ (Chế độ đăng ký/tạo ban đầu)
            device.setUser(user);
            device.setName("My Smart Garden - " + deviceUid.substring(deviceUid.length() - 2)); // Tên mặc định
            deviceRepository.save(device);
            return deviceMapper.toDto(device);
        } else {
            // Thiết bị chưa từng tồn tại (Tạo mới trong quá trình claim)
            DeviceEntity newDevice = new DeviceEntity();
            newDevice.setDeviceUid(deviceUid);
            newDevice.setName("New Device - " + deviceUid);
            newDevice.setUser(user);

            DeviceEntity savedDevice = deviceRepository.save(newDevice);
            return deviceMapper.toDto(savedDevice);
        }
    }

    /**
     * 2. Lấy danh sách các thiết bị CỦA TÔI.
     */
    public List<DeviceDto> getDevicesByUser(Long userId) {
        List<DeviceEntity> devices = deviceRepository.findAllByUserId(userId);
        return devices.stream()
                .map(deviceMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 3. Kiểm tra quyền sở hữu thiết bị (QUAN TRỌNG CHO BẢO MẬT).
     * Phải được gọi trước khi thực hiện bất kỳ thao tác nào với thiết bị (như lấy trạng thái, gửi lệnh).
     */
    public void validateDeviceOwnership(String deviceUid, Long userId) throws AccessDeniedException {
        DeviceEntity device = deviceRepository.findByDeviceUid(deviceUid)
                .orElseThrow(() -> new AppException(ErrorCode.DEVICE_NOT_FOUND));

        // Kiểm tra xem user_id của thiết bị có khớp với userId đang request không
        if (device.getUser() == null || !device.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User is not authorized to access device " + deviceUid);
        }
    }
}
