package com.example.demo.service;

import com.example.demo.model.request.UserCreateRequest;
import com.example.demo.model.response.UserResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponse createUser(UserCreateRequest userRequest);

    UserResponse getUserById(Long id);


    UserResponse updateUser(Long id, UserCreateRequest userRequest);

    void softdeleteUser(Long id);

    UserResponse assignRoleToUser(Long userId, String roleId);

    UserResponse removeRoleFromUser(Long userId, String roleId);

    void restoreUser(Long id);
}
