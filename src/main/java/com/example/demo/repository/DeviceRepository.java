package com.example.demo.repository;

import com.example.demo.model.entity.DeviceEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends  BaseRepository<DeviceEntity, Long> {
}
