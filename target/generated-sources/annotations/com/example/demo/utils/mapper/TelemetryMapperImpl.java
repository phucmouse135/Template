package com.example.demo.utils.mapper;

import com.example.demo.dto.DeviceDto;
import com.example.demo.dto.TelemetryLogDto;
import com.example.demo.model.entity.DeviceEntity;
import com.example.demo.model.entity.TelemetryLog;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-22T07:21:23+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Microsoft)"
)
@Component
public class TelemetryMapperImpl implements TelemetryMapper {

    @Override
    public TelemetryLog toEntity(TelemetryLogDto dto) {
        if ( dto == null ) {
            return null;
        }

        TelemetryLog.TelemetryLogBuilder<?, ?> telemetryLog = TelemetryLog.builder();

        telemetryLog.createdAt( dto.getCreatedAt() );
        telemetryLog.updatedAt( dto.getUpdatedAt() );
        telemetryLog.deletedAt( dto.getDeletedAt() );
        telemetryLog.id( dto.getId() );
        telemetryLog.device( deviceDtoToDeviceEntity( dto.getDevice() ) );
        telemetryLog.logTime( dto.getLogTime() );
        telemetryLog.temperature( dto.getTemperature() );
        telemetryLog.airHumidity( dto.getAirHumidity() );
        telemetryLog.lightLevel( dto.getLightLevel() );
        telemetryLog.soilMoisture( dto.getSoilMoisture() );

        return telemetryLog.build();
    }

    @Override
    public TelemetryLogDto toDto(TelemetryLog entity) {
        if ( entity == null ) {
            return null;
        }

        TelemetryLogDto.TelemetryLogDtoBuilder telemetryLogDto = TelemetryLogDto.builder();

        telemetryLogDto.id( entity.getId() );
        telemetryLogDto.device( deviceEntityToDeviceDto( entity.getDevice() ) );
        telemetryLogDto.logTime( entity.getLogTime() );
        telemetryLogDto.temperature( entity.getTemperature() );
        telemetryLogDto.airHumidity( entity.getAirHumidity() );
        telemetryLogDto.lightLevel( entity.getLightLevel() );
        telemetryLogDto.soilMoisture( entity.getSoilMoisture() );
        telemetryLogDto.createdAt( entity.getCreatedAt() );
        telemetryLogDto.updatedAt( entity.getUpdatedAt() );
        telemetryLogDto.deletedAt( entity.getDeletedAt() );

        return telemetryLogDto.build();
    }

    protected DeviceEntity deviceDtoToDeviceEntity(DeviceDto deviceDto) {
        if ( deviceDto == null ) {
            return null;
        }

        DeviceEntity.DeviceEntityBuilder<?, ?> deviceEntity = DeviceEntity.builder();

        deviceEntity.createdAt( deviceDto.getCreatedAt() );
        deviceEntity.updatedAt( deviceDto.getUpdatedAt() );
        deviceEntity.deletedAt( deviceDto.getDeletedAt() );
        deviceEntity.id( deviceDto.getId() );
        deviceEntity.deviceUid( deviceDto.getDeviceUid() );
        deviceEntity.name( deviceDto.getName() );

        return deviceEntity.build();
    }

    protected DeviceDto deviceEntityToDeviceDto(DeviceEntity deviceEntity) {
        if ( deviceEntity == null ) {
            return null;
        }

        DeviceDto.DeviceDtoBuilder deviceDto = DeviceDto.builder();

        deviceDto.id( deviceEntity.getId() );
        deviceDto.deviceUid( deviceEntity.getDeviceUid() );
        deviceDto.name( deviceEntity.getName() );
        deviceDto.createdAt( deviceEntity.getCreatedAt() );
        deviceDto.updatedAt( deviceEntity.getUpdatedAt() );
        deviceDto.deletedAt( deviceEntity.getDeletedAt() );

        return deviceDto.build();
    }
}
