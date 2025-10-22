package com.example.demo.utils.mapper;

import com.example.demo.dto.TelemetryLogDto;
import com.example.demo.model.entity.TelemetryLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TelemetryMapper {

    TelemetryLog toEntity(TelemetryLogDto dto);

    TelemetryLogDto toDto(TelemetryLog entity);
}
