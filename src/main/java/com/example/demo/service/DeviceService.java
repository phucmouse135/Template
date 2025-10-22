package com.example.demo.service;

import com.example.demo.dto.DeviceDto;
import com.example.demo.model.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public interface DeviceService {
    public DeviceDto claimDevice(String deviceUid, UserEntity user);
    List<DeviceDto> getDevicesByUser(Long userId);
    public void validateDeviceOwnership(String deviceUid, Long userId) throws AccessDeniedException;
}
