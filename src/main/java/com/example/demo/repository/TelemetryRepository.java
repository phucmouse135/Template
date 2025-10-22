package com.example.demo.repository;

import com.example.demo.model.entity.DeviceEntity;
import com.example.demo.model.entity.TelemetryLog;
import org.springframework.stereotype.Repository;

@Repository
public interface TelemetryRepository extends  BaseRepository<TelemetryLog, Long> {
}
