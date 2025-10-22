package com.example.demo.utils.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.model.request.UserCreateRequest;
import com.example.demo.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserCreateRequest request);

    UserDto toDto(UserEntity user);

    UserResponse toResponse(UserEntity user);

    void updateEntityFromCreateRequest(UserCreateRequest request,@MappingTarget UserEntity user);
}
