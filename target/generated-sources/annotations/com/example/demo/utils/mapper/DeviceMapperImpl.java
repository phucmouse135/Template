package com.example.demo.utils.mapper;

import com.example.demo.dto.DeviceDto;
import com.example.demo.model.entity.DeviceEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-22T07:21:23+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Microsoft)"
)
@Component
public class DeviceMapperImpl implements DeviceMapper {

    @Override
    public DeviceEntity toEntity(DeviceDto dto) {
        if ( dto == null ) {
            return null;
        }

        DeviceEntity.DeviceEntityBuilder<?, ?> deviceEntity = DeviceEntity.builder();

        deviceEntity.createdAt( dto.getCreatedAt() );
        deviceEntity.updatedAt( dto.getUpdatedAt() );
        deviceEntity.deletedAt( dto.getDeletedAt() );
        deviceEntity.id( dto.getId() );
        deviceEntity.deviceUid( dto.getDeviceUid() );
        deviceEntity.name( dto.getName() );

        return deviceEntity.build();
    }

    @Override
    public DeviceDto toDto(DeviceEntity entity) {
        if ( entity == null ) {
            return null;
        }

        DeviceDto.DeviceDtoBuilder deviceDto = DeviceDto.builder();

        deviceDto.id( entity.getId() );
        deviceDto.deviceUid( entity.getDeviceUid() );
        deviceDto.name( entity.getName() );
        deviceDto.createdAt( entity.getCreatedAt() );
        deviceDto.updatedAt( entity.getUpdatedAt() );
        deviceDto.deletedAt( entity.getDeletedAt() );

        return deviceDto.build();
    }
}
