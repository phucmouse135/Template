package com.example.demo.repository;

import com.example.demo.model.entity.DeviceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends  BaseRepository<DeviceEntity, Long> {

    // findByDeviceUid(deviceUid)
    @Query("SELECT d FROM DeviceEntity d WHERE d.deviceUid = ?1")
    Optional<DeviceEntity> findByDeviceUid(String deviceUid);

    // findAllByUserId
    @Query("SELECT d FROM DeviceEntity d WHERE d.user.id = ?1")
    List<DeviceEntity> findAllByUserId(Long userId);
}
