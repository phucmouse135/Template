package com.example.demo.service;

import com.example.demo.dto.DeviceStateDTO;
import org.springframework.stereotype.Service;

@Service
public interface TelemetryService {
    public void saveTelemetryLog(DeviceStateDTO updatedState);
}
