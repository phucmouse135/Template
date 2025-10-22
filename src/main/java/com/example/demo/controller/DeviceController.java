package com.example.demo.controller;

import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Device Controller")
public class DeviceController {
    private final UserService userService;
}
