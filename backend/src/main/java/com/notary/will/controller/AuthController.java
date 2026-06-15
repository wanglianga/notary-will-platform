package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.dto.LoginRequest;
import com.notary.will.dto.LoginResponse;
import com.notary.will.dto.RegisterRequest;
import com.notary.will.entity.User;
import com.notary.will.security.JwtTokenProvider;
import com.notary.will.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        Map<String, Object> data = new HashMap<>();
        data.put("token", response.getToken());
        data.put("role", response.getRole());
        data.put("username", response.getUsername());
        Map<String, Object> user = new HashMap<>();
        user.put("id", response.getUserId());
        user.put("username", response.getUsername());
        user.put("role", response.getRole());
        user.put("name", response.getUsername());
        data.put("user", user);
        return ApiResponse.ok(data);
    }

    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(userService.register(request));
    }

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> getCurrentUser(Authentication auth) {
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole().name());
        data.put("name", user.getRealName() != null ? user.getRealName() : user.getUsername());
        data.put("realName", user.getRealName());
        data.put("phone", user.getPhone());
        return ApiResponse.ok(data);
    }
}
