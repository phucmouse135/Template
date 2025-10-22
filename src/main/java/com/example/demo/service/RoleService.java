package com.example.demo.service;

import com.example.demo.model.request.RoleRequest;
import com.example.demo.model.response.RoleResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {
    RoleResponse createRole(RoleRequest request);

    List<RoleResponse> getAllRoles();
}
