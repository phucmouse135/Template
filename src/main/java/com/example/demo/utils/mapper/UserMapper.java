package com.example.demo.utils.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.model.request.UserCreateRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserCreateRequest request);

    UserDto toDto(UserEntity user);
}
