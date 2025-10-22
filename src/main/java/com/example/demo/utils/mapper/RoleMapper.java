package com.example.demo.utils.mapper;

import com.example.demo.model.entity.RoleEntity;
import com.example.demo.model.request.RoleRequest;
import com.example.demo.model.response.RoleResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleEntity toEntity(RoleRequest roleRequest);
    RoleResponse toResponse(RoleEntity roleEntity);
}
