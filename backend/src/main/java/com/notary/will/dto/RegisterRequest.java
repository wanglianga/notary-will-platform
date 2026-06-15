package com.notary.will.dto;

import com.notary.will.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotNull
    private UserRole role;
    private String realName;
    private String phone;
}
