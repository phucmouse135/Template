package com.example.demo.service.impl;

import com.example.demo.model.entity.RoleEntity;
import com.example.demo.model.request.RoleRequest;
import com.example.demo.model.response.RoleResponse;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.RoleService;
import com.example.demo.utils.mapper.RoleMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleServiceImpl implements RoleService {
    RoleMapper roleMapper;
    RoleRepository roleRepository;

    /**
     * Create role
     * @param request
     * @return
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse createRole(RoleRequest request) {
        log.info("Creating role: {}", request.getName());
        RoleEntity role = roleMapper.toEntity(request);
        role = roleRepository.save(role);
        return roleMapper.toResponse(role);
    }

    /**
     * Get all roles
     * @return
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getAllRoles() {
        log.info("Getting all roles");
        return roleRepository.findAll().stream().map(roleMapper::toResponse).toList();
    }

}
