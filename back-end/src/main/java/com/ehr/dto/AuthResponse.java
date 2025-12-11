package com.ehr.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private String sessionId;
    private String userType;  // "PATIENT" or "STAFF"
    private String userId;
    private String fullName;
    private String role;

    public AuthResponse(String message, String sessionId, String userType) {
        this.message = message;
        this.sessionId = sessionId;
        this.userType = userType;
    }
}