package com.vsignai.backend.dto.response;

import com.vsignai.backend.enums.subscription.PlanCode;
import com.vsignai.backend.enums.user.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private UserRole role;
    private PlanCode plan;
}