package com.ehr.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDto {

    @NotBlank(message = "Email or phone number is required")
    private String identifier;  // Can be email or phone number

    @NotBlank(message = "Password is required")
    private String password;
}