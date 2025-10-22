package com.example.demo.service.impl;

import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.entity.RoleEntity;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.model.request.UserCreateRequest;
import com.example.demo.model.response.UserResponse;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.utils.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    /**
     * Create user
     *
     * @param userRequest
     * @return
     */
    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest userRequest) {
        log.info("Creating user: {}", userRequest.getUsername());
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        UserEntity user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        HashSet<RoleEntity> roles = new HashSet<>();
        for (String roleId : userRequest.getRoleIds()) {
            RoleEntity role =
                    roleRepository.findById(roleId).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
            roles.add(role);
        }
        user.setRoles(roles);
        user = userRepository.save(user);
        log.info("Created user: {}", user.getId());
        return userMapper.toResponse(user);
    }

    /**
     * Get user by id
     *
     * @param id
     * @return
     */
    @Override
    @Cacheable(value = "users", key = "#id", cacheManager = "caffeineCacheManager")
    @PostAuthorize("hasRole('ADMIN') or returnObject.username == authentication.name")
    public UserResponse getUserById(Long id) {
        log.info("Getting user by id: {}", id);
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toResponse(user);
    }


    /**
     * Update user
     *
     * @param id
     * @param userRequest
     * @return
     */
    @Override
    @Transactional
    @CachePut(value = "users", key = "#id", cacheManager = "caffeineCacheManager")
    @PostAuthorize("hasRole('ADMIN') or returnObject.username == authentication.name")
    public UserResponse updateUser(Long id, UserCreateRequest userRequest) {
        log.info("Updating user by id: {}", id);
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateEntityFromCreateRequest(userRequest, user);
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        Set<RoleEntity> roles = new HashSet<>();
        for (String roleId : userRequest.getRoleIds()) {
            RoleEntity role =
                    roleRepository.findById(roleId).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
            roles.add(role);
        }
        user.setRoles(roles);
        user = userRepository.save(user);
        log.info("Updated user: {}", user.getId());
        return userMapper.toResponse(user);
    }

    /**
     * Soft delete user by id
     *
     * @param id
     */
    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id", cacheManager = "caffeineCacheManager")
    @PreAuthorize("hasRole('ADMIN') or @ownershipSecurity.isOwner(authentication, #id)")
    public void softdeleteUser(Long id) {
        log.info("Soft deleting user by id: {}", id);
        userRepository.softDeleteByIds(List.of(id));
    }

    /**
     * Assign role to user
     *
     * @param userId
     * @param roleId
     * @return
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse assignRoleToUser(Long userId, String roleId) {
        log.info("Assigning role {} to user {}", roleId, userId);
        UserEntity user =
                userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        RoleEntity role =
                roleRepository.findById(roleId).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        user.getRoles().add(role);
        user = userRepository.save(user);
        log.info("Assigned role {} to user {}", roleId, userId);
        return userMapper.toResponse(user);
    }

    /**
     * Remove role from user
     *
     * @param userId
     * @param roleId
     * @return
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse removeRoleFromUser(Long userId, String roleId) {
        log.info("Removing role {} from user {}", roleId, userId);
        UserEntity user =
                userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        RoleEntity role =
                roleRepository.findById(roleId).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        user.getRoles().remove(role);
        user = userRepository.save(user);
        log.info("Removed role {} from user {}", roleId, userId);
        return userMapper.toResponse(user);
    }

    /**
     * Restore user by id
     *
     * @param id
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @ownershipSecurity.isOwner(authentication, #id)")
    public void restoreUser(Long id) {
        log.info("Restoring user by id: {}", id);
        userRepository.restoreById(id);
    }
}
